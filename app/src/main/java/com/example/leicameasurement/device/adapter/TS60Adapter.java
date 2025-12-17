package com.example.leicameasurement.device.adapter;

import com.example.leicameasurement.device.bluetooth.BluetoothLinkManager;
import com.example.leicameasurement.device.connection.IConnectionChannel;
import com.example.leicameasurement.device.protocol.GeoComCommandBuilder;
import com.example.leicameasurement.device.protocol.GeoComResponseParser;
import com.example.leicameasurement.device.protocol.InstrumentException;

import java.io.IOException;

/**
 * TS60 全站仪适配器
 * 实现 InstrumentAdapter 接口，提供 TS60 特定的功能实现
 */
public class TS60Adapter implements InstrumentAdapter {

    private IConnectionChannel connectionChannel;
    private GeoComCommandBuilder commandBuilder;
    private GeoComResponseParser responseParser;
    private boolean initialized = false;

    // ==================== 构造函数 ====================

    /**
     * 无参构造函数（保留，用于 initialize() 模式）
     */
    public TS60Adapter() {
        this.commandBuilder = new GeoComCommandBuilder();
        this.responseParser = new GeoComResponseParser();
    }

    /**
     * 带 BluetoothLinkManager 参数的构造函数（新增，用于 InstrumentFactory）
     * @param linkManager 蓝牙链路管理器
     */
    public TS60Adapter(BluetoothLinkManager linkManager) {
        this.commandBuilder = new GeoComCommandBuilder();
        this.responseParser = new GeoComResponseParser();

        // 注意：这里我们不直接使用 linkManager，而是等待 initialize() 调用
        // 如果你想直接使用 linkManager，需要创建一个 IConnectionChannel 包装器
        // 或者修改架构让 BluetoothLinkManager 实现 IConnectionChannel 接口
    }

    // ==================== InstrumentAdapter 接口实现 ====================

    @Override
    public void initialize(IConnectionChannel connectionChannel) {
        this.connectionChannel = connectionChannel;
        this.initialized = true;
    }

    @Override
    public boolean isConnected() {
        return initialized && connectionChannel != null && connectionChannel.isConnected();
    }

    @Override
    public boolean setStation(double stationX, double stationY, double stationH, double instrumentHeight)
            throws InstrumentException {

        if (!isConnected()) {
            throw new InstrumentException("仪器未连接");
        }

        try {
            // 构建设置测站坐标的GeoCOM指令
            byte[] command = commandBuilder.buildSetStationCommand(stationX, stationY, stationH);
            connectionChannel.sendData(command);

            // 接收响应
            byte[] response = connectionChannel.receiveData();
            boolean success = responseParser.parseSetStationResponse(response);

            if (success) {
                // 设置仪器高
                byte[] heightCommand = commandBuilder.buildSetInstrumentHeightCommand(instrumentHeight);
                connectionChannel.sendData(heightCommand);
                byte[] heightResponse = connectionChannel.receiveData();
                return responseParser.parseSimpleResponse(heightResponse);
            }

            return false;

        } catch (IOException e) {
            throw new InstrumentException("设置测站失败: " + e.getMessage(), "SET_STATION_ERROR", "TS60");
        } catch (Exception e) {
            throw new InstrumentException("设置测站解析失败: " + e.getMessage(), "SET_STATION_PARSE_ERROR", "TS60");
        }
    }

    @Override
    public double[] measureBacksight(double prismHeight) throws InstrumentException {
        return performMeasurement(prismHeight, "BACKSIGHT", MeasureMode.PRECISION);
    }

    @Override
    public double[] measureForepoint(double prismHeight) throws InstrumentException {
        return performMeasurement(prismHeight, "FOREPOINT", MeasureMode.STANDARD);
    }

    @Override
    public double[] measureDetailPoint(double prismHeight, MeasureMode mode) throws InstrumentException {
        return performMeasurement(prismHeight, "DETAIL_POINT", mode);
    }

    @Override
    public InstrumentInfo getInstrumentInfo() throws InstrumentException {
        if (!isConnected()) {
            throw new InstrumentException("仪器未连接");
        }

        try {
            // 获取仪器型号
            byte[] modelCommand = commandBuilder.buildGetModelCommand();
            connectionChannel.sendData(modelCommand);
            byte[] modelResponse = connectionChannel.receiveData();
            String model = responseParser.parseModelResponse(modelResponse);

            // 获取序列号
            byte[] serialCommand = commandBuilder.buildGetSerialNumberCommand();
            connectionChannel.sendData(serialCommand);
            byte[] serialResponse = connectionChannel.receiveData();
            String serialNumber = responseParser.parseSerialNumberResponse(serialResponse);

            return new InstrumentInfo(model, serialNumber, "1.0", "Leica");

        } catch (IOException e) {
            throw new InstrumentException("获取仪器信息失败: " + e.getMessage(), "GET_INFO_ERROR", "TS60");
        } catch (Exception e) {
            throw new InstrumentException("解析仪器信息失败: " + e.getMessage(), "PARSE_INFO_ERROR", "TS60");
        }
    }

    @Override
    public InstrumentStatus getStatus() throws InstrumentException {
        if (!isConnected()) {
            throw new InstrumentException("仪器未连接");
        }

        try {
            // 获取仪器状态
            byte[] statusCommand = commandBuilder.buildGetStatusCommand();
            connectionChannel.sendData(statusCommand);
            byte[] statusResponse = connectionChannel.receiveData();

            return responseParser.parseStatusResponse(statusResponse);

        } catch (IOException e) {
            throw new InstrumentException("获取仪器状态失败: " + e.getMessage(), "GET_STATUS_ERROR", "TS60");
        } catch (Exception e) {
            throw new InstrumentException("解析仪器状态失败: " + e.getMessage(), "PARSE_STATUS_ERROR", "TS60");
        }
    }

    @Override
    public void close() {
        if (connectionChannel != null && connectionChannel.isConnected()) {
            try {
                // 发送关闭指令
                byte[] closeCommand = commandBuilder.buildCloseCommand();
                connectionChannel.sendData(closeCommand);
            } catch (Exception e) {
                // 忽略关闭时的异常
            } finally {
                connectionChannel.disconnect();
            }
        }
        initialized = false;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 执行测量的通用方法
     */
    private double[] performMeasurement(double prismHeight, String measurementType, MeasureMode mode)
            throws InstrumentException {

        if (!isConnected()) {
            throw new InstrumentException("仪器未连接");
        }

        try {
            // 1. 设置棱镜高
            byte[] prismCommand = commandBuilder.buildSetPrismHeightCommand(prismHeight);
            connectionChannel.sendData(prismCommand);
            byte[] prismResponse = connectionChannel.receiveData();

            if (!responseParser.parseSimpleResponse(prismResponse)) {
                throw new InstrumentException("设置棱镜高失败", "SET_PRISM_ERROR", "TS60");
            }

            // 2. 设置测量模式
            byte[] modeCommand = buildMeasureModeCommand(mode);
            connectionChannel.sendData(modeCommand);
            byte[] modeResponse = connectionChannel.receiveData();

            if (!responseParser.parseSimpleResponse(modeResponse)) {
                throw new InstrumentException("设置测量模式失败", "SET_MODE_ERROR", "TS60");
            }

            // 3. 执行测量
            byte[] measureCommand = buildMeasurementCommand(measurementType);
            connectionChannel.sendData(measureCommand);
            byte[] measureResponse = connectionChannel.receiveData();

            // 4. 解析测量结果 [水平角, 竖直角, 斜距, X坐标, Y坐标, Z坐标]
            return responseParser.parseMeasurementResponse(measureResponse);

        } catch (IOException e) {
            throw new InstrumentException(measurementType + "测量失败: " + e.getMessage(),
                    "MEASURE_" + measurementType + "_ERROR", "TS60");
        } catch (Exception e) {
            throw new InstrumentException("解析测量结果失败: " + e.getMessage(),
                    "PARSE_MEASUREMENT_ERROR", "TS60");
        }
    }

    /**
     * 构建测量模式指令
     */
    private byte[] buildMeasureModeCommand(MeasureMode mode) {
        switch (mode) {
            case PRECISION:
                return commandBuilder.buildSetPrecisionModeCommand();
            case FAST:
                return commandBuilder.buildSetFastModeCommand();
            case STANDARD:
            default:
                return commandBuilder.buildSetStandardModeCommand();
        }
    }

    /**
     * 构建测量类型指令
     */
    private byte[] buildMeasurementCommand(String measurementType) {
        switch (measurementType) {
            case "BACKSIGHT":
                return commandBuilder.buildBacksightMeasurementCommand();
            case "FOREPOINT":
                return commandBuilder.buildForepointMeasurementCommand();
            case "DETAIL_POINT":
            default:
                return commandBuilder.buildDetailPointMeasurementCommand();
        }
    }
}
