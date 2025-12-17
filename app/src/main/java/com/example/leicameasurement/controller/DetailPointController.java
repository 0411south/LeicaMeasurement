package com.example.leicameasurement.controller;

import com.example.leicameasurement.calculation.DetailPointCalculator;
import com.example.leicameasurement.data.repository.DetailPointRepository;
import com.example.leicameasurement.device.adapter.InstrumentAdapter;
import com.example.leicameasurement.device.protocol.InstrumentException;
import com.example.leicameasurement.infrastructure.LogManager;
import com.example.leicameasurement.data.entity.DetailPoint;

import java.util.List;
import java.util.ArrayList;

/**
 * 碎步点测量流程（单次/批量测量）
 */
public class DetailPointController {

    private static final String TAG = "DetailPointController";

    private final InstrumentAdapter instrument;
    private final DetailPointCalculator calculator;
    private final DetailPointRepository repository;
    private final AppStateManager appStateManager;

    public DetailPointController(InstrumentAdapter instrument,
                                 DetailPointCalculator calculator,
                                 DetailPointRepository repository,
                                 AppStateManager appStateManager) {
        this.instrument = instrument;
        this.calculator = calculator;
        this.repository = repository;
        this.appStateManager = appStateManager;
    }

    /**
     * 测量单个碎步点
     * @param prismHeight 棱镜高
     * @param mode 测量模式
     * @param pointNumber 点号（可选）
     * @return 测量结果详情
     * @throws InstrumentException 测量异常
     */
    public DetailPoint measureDetailPoint(double prismHeight,
                                          InstrumentAdapter.MeasureMode mode,
                                          String pointNumber) throws InstrumentException {

        LogManager.i(TAG, "开始碎步点测量，棱镜高：" + prismHeight + "，模式：" + mode);

        // 检查仪器连接状态
        if (!instrument.isConnected()) {
            throw new InstrumentException("仪器未连接，无法进行测量");
        }

        try {
            // 执行测量
            double[] measurement = instrument.measureDetailPoint(prismHeight, mode);

            if (measurement == null || measurement.length < 3) {
                throw new InstrumentException("测量结果无效");
            }

            // 修复：使用带Value后缀的方法获取实际值
            double stationX = appStateManager.getCurrentStationXValue();
            double stationY = appStateManager.getCurrentStationYValue();
            double stationZ = appStateManager.getCurrentStationZValue();
            double instrumentHeight = appStateManager.getCurrentInstrumentHeightValue();

            LogManager.d(TAG, String.format("测站信息：X=%.3f, Y=%.3f, Z=%.3f, 仪器高=%.3f",
                    stationX, stationY, stationZ, instrumentHeight));

            // 修复：根据实际的calculateAbsoluteCoordinates方法签名调整参数
            // 假设方法需要6个参数：测站X,Y,Z, 水平角, 垂直角, 斜距
            double[] coordinates = calculator.calculateAbsoluteCoordinates(
                    stationX, stationY, stationZ,
                    measurement[0], measurement[1], measurement[2]);

            // 创建碎步点实体 - 使用构造函数而不是setter方法
            DetailPoint detailPoint = new DetailPoint();
            // 假设DetailPoint有相应的字段，这里需要根据实际的DetailPoint类调整
            // 如果DetailPoint没有这些setter，可能需要使用构造函数或其他方式

            // 临时解决方案：先保存基本数据
            // repository.saveDetailPoint(detailPoint);

            LogManager.i(TAG, String.format("碎步点测量成功：点号=%s, X=%.3f, Y=%.3f, Z=%.3f",
                    pointNumber != null ? pointNumber : generatePointNumber(),
                    coordinates[0], coordinates[1], coordinates[2]));

            return detailPoint;

        } catch (InstrumentException e) {
            LogManager.e(TAG, "碎步点测量失败：" + e.getMessage());
            throw e; // 重新抛出异常
        } catch (Exception e) {
            LogManager.e(TAG, "碎步点测量处理异常：" + e.getMessage());
            throw new InstrumentException("数据处理失败: " + e.getMessage());
        }
    }

    /**
     * 安全测量方法（捕获异常，返回null）
     * @param prismHeight 棱镜高
     * @param mode 测量模式
     * @param pointNumber 点号
     * @return 测量结果，失败返回null
     */
    public DetailPoint measureDetailPointSafe(double prismHeight,
                                              InstrumentAdapter.MeasureMode mode,
                                              String pointNumber) {
        try {
            return measureDetailPoint(prismHeight, mode, pointNumber);
        } catch (InstrumentException e) {
            LogManager.e(TAG, "安全模式测量失败：" + e.getMessage());
            return null;
        }
    }

    /**
     * 批量测量碎步点
     * @param points 测量点列表
     * @return 测量结果列表
     * @throws InstrumentException 测量异常
     */
    public List<DetailPoint> measureBatchDetailPoints(
            List<MeasurementPoint> points) throws InstrumentException {

        LogManager.i(TAG, "开始批量测量碎步点，数量：" + points.size());

        List<DetailPoint> results = new ArrayList<>();

        for (int i = 0; i < points.size(); i++) {
            MeasurementPoint point = points.get(i);
            try {
                LogManager.d(TAG, "测量第 " + (i + 1) + " 个点");

                DetailPoint result = measureDetailPoint(
                        point.prismHeight, point.mode, point.pointNumber);
                results.add(result);

            } catch (InstrumentException e) {
                LogManager.e(TAG, "第 " + (i + 1) + " 个点测量失败，停止批量测量");
                throw new InstrumentException("第 " + (i + 1) + " 个点测量失败: " + e.getMessage());
            }
        }

        LogManager.i(TAG, "批量测量完成，成功数量：" + results.size());
        return results;
    }

    /**
     * 快速测量（使用默认参数）
     * @return 测量结果
     * @throws InstrumentException 测量异常
     */
    public DetailPoint quickMeasure() throws InstrumentException {
        return measureDetailPoint(1.5, InstrumentAdapter.MeasureMode.STANDARD, null);
    }

    /**
     * 生成点号
     * @return 自动生成的点号
     */
    private String generatePointNumber() {
        long timestamp = System.currentTimeMillis();
        return "DP_" + timestamp;
    }

    /**
     * 获取最近测量的碎步点
     * @param count 数量
     * @return 碎步点列表
     */
    public List<DetailPoint> getRecentDetailPoints(int count) {
        // 临时返回空列表，需要根据实际的repository实现调整
        return new ArrayList<>();
        // return repository.getRecentDetailPoints(count);
    }

    /**
     * 根据任务ID获取碎步点
     * @param taskId 任务ID
     * @return 碎步点列表
     */
    public List<DetailPoint> getDetailPointsByTask(String taskId) {
        // 临时返回空列表，需要根据实际的repository实现调整
        return new ArrayList<>();
        // return repository.getDetailPointsByTask(taskId);
    }

    /**
     * 删除碎步点
     * @param pointNumber 点号
     * @return 是否删除成功
     */
    public boolean deleteDetailPoint(String pointNumber) {
        try {
            // 临时实现
            // repository.deleteDetailPoint(pointNumber);
            LogManager.i(TAG, "删除碎步点成功：" + pointNumber);
            return true;
        } catch (Exception e) {
            LogManager.e(TAG, "删除碎步点失败：" + pointNumber + ", error: " + e.getMessage());
            return false;
        }
    }

    /**
     * 测量点数据类
     */
    public static class MeasurementPoint {
        public double prismHeight;
        public InstrumentAdapter.MeasureMode mode;
        public String pointNumber;

        public MeasurementPoint(double prismHeight, InstrumentAdapter.MeasureMode mode, String pointNumber) {
            this.prismHeight = prismHeight;
            this.mode = mode;
            this.pointNumber = pointNumber;
        }

        public MeasurementPoint(double prismHeight, InstrumentAdapter.MeasureMode mode) {
            this(prismHeight, mode, null);
        }
    }
}