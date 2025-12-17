package com.example.leicameasurement.device.adapter;

import com.example.leicameasurement.device.connection.IConnectionChannel;
import com.example.leicameasurement.device.protocol.InstrumentException;

/**
 * 仪器适配器接口：定义所有全站仪的标准化操作
 * 核心职责：屏蔽不同型号仪器的指令差异
 */
public interface InstrumentAdapter {

    /**
     * 测量模式枚举
     */
    enum MeasureMode {
        STANDARD,  // 标准测量
        PRECISION, // 精密测量
        FAST       // 快速测量
    }

    /**
     * 初始化适配器，注入连接通道
     * @param connectionChannel 连接通道（蓝牙或WiFi）
     */
    void initialize(IConnectionChannel connectionChannel);

    /**
     * 获取连接状态
     * @return true=连接正常
     */
    boolean isConnected();

    /**
     * 设置测站
     * @param stationX X坐标
     * @param stationY Y坐标
     * @param stationH H坐标
     * @param instrumentHeight 仪器高
     * @return true=成功
     * @throws InstrumentException 仪器操作异常
     */
    boolean setStation(double stationX, double stationY, double stationH, double instrumentHeight)
            throws InstrumentException;

    /**
     * 测量后视点
     * @param prismHeight 棱镜高
     * @return 测量数据（水平角、竖直角、斜距等）
     * @throws InstrumentException 测量异常
     */
    double[] measureBacksight(double prismHeight) throws InstrumentException;

    /**
     * 测量前视点
     * @param prismHeight 棱镜高
     * @return 测量数据（水平角、竖直角、斜距等）
     * @throws InstrumentException 测量异常
     */
    double[] measureForepoint(double prismHeight) throws InstrumentException;

    /**
     * 测量碎步点
     * @param prismHeight 棱镜高
     * @param mode 测量模式
     * @return 测量数据（水平角、竖直角、斜距等）
     * @throws InstrumentException 测量异常
     */
    double[] measureDetailPoint(double prismHeight, MeasureMode mode) throws InstrumentException;

    /**
     * 获取仪器信息
     * @return 仪器型号、序列号等信息
     * @throws InstrumentException 仪器操作异常
     */
    InstrumentInfo getInstrumentInfo() throws InstrumentException;

    /**
     * 检查仪器状态
     * @return 仪器状态信息
     * @throws InstrumentException 仪器操作异常
     */
    InstrumentStatus getStatus() throws InstrumentException;

    /**
     * 关闭适配器，释放资源
     */
    void close();
}