package com.example.leicameasurement.device.adapter;

import com.example.leicameasurement.device.bluetooth.BluetoothLinkManager;
import com.example.leicameasurement.device.connection.IConnectionChannel;
import com.example.leicameasurement.device.protocol.GeoComCommandBuilder;
import com.example.leicameasurement.device.protocol.GeoComResponseParser;
import com.example.leicameasurement.device.protocol.InstrumentException;

public class TS30Adapter implements InstrumentAdapter {

    private BluetoothLinkManager mLinkManager;
    private GeoComCommandBuilder mCommandBuilder;
    private GeoComResponseParser mResponseParser;

    public TS30Adapter(BluetoothLinkManager linkManager) {
        this.mLinkManager = linkManager;
        this.mCommandBuilder = new GeoComCommandBuilder();
        this.mResponseParser = new GeoComResponseParser();
    }

    @Override
    public void initialize(IConnectionChannel connectionChannel) {
        // TS30Adapter 使用构造函数注入，这个方法可以留空或抛出异常
        // 如果你想统一使用 initialize 模式，可以重构构造函数
    }

    @Override
    public boolean isConnected() {
        return mLinkManager != null && mLinkManager.isConnected();
    }

    @Override
    public boolean setStation(double stationX, double stationY, double stationH, double instrumentHeight)
            throws InstrumentException {
        try {
            // 修复：buildSetStationCommand 只需要 3 个参数
            byte[] command = mCommandBuilder.buildSetStationCommand(stationX, stationY, stationH);
            mLinkManager.sendCommand(command);

            byte[] response = mLinkManager.receiveResponse();
            boolean success = mResponseParser.parseSetStationResponse(response);

            if (success) {
                // 单独设置仪器高
                byte[] heightCommand = mCommandBuilder.buildSetInstrumentHeightCommand(instrumentHeight);
                mLinkManager.sendCommand(heightCommand);
                byte[] heightResponse = mLinkManager.receiveResponse();
                return mResponseParser.parseSimpleResponse(heightResponse);
            }

            return false;
        } catch (Exception e) {
            throw new InstrumentException("设置测站失败: " + e.getMessage(), "SET_STATION_ERROR", "TS30");
        }
    }

    @Override
    public double[] measureBacksight(double prismHeight) throws InstrumentException {
        return performMeasurement(prismHeight, MeasureMode.PRECISION);
    }

    @Override
    public double[] measureForepoint(double prismHeight) throws InstrumentException {
        return performMeasurement(prismHeight, MeasureMode.STANDARD);
    }

    @Override
    public double[] measureDetailPoint(double prismHeight, MeasureMode mode) throws InstrumentException {
        return performMeasurement(prismHeight, mode);
    }

    @Override
    public InstrumentInfo getInstrumentInfo() throws InstrumentException {
        try {
            byte[] command = mCommandBuilder.buildGetModelCommand();
            mLinkManager.sendCommand(command);
            byte[] response = mLinkManager.receiveResponse();

            String model = mResponseParser.parseModelResponse(response);
            return new InstrumentInfo(model, "Unknown", "1.0", "Leica");
        } catch (Exception e) {
            throw new InstrumentException("获取仪器信息失败: " + e.getMessage(), "GET_INFO_ERROR", "TS30");
        }
    }

    @Override
    public InstrumentStatus getStatus() throws InstrumentException {
        try {
            byte[] command = mCommandBuilder.buildGetStatusCommand();
            mLinkManager.sendCommand(command);
            byte[] response = mLinkManager.receiveResponse();

            return mResponseParser.parseStatusResponse(response);
        } catch (Exception e) {
            throw new InstrumentException("获取状态失败: " + e.getMessage(), "GET_STATUS_ERROR", "TS30");
        }
    }

    @Override
    public void close() {
        if (mLinkManager != null) {
            try {
                byte[] command = mCommandBuilder.buildCloseCommand();
                mLinkManager.sendCommand(command);
            } catch (Exception e) {
                // 忽略关闭时的异常
            } finally {
                mLinkManager.disconnect();
            }
        }
    }

    /**
     * 执行测量的通用方法
     */
    private double[] performMeasurement(double prismHeight, MeasureMode mode) throws InstrumentException {
        try {
            // 1. 设置棱镜高
            byte[] prismCommand = mCommandBuilder.buildSetPrismHeightCommand(prismHeight);
            mLinkManager.sendCommand(prismCommand);
            byte[] prismResponse = mLinkManager.receiveResponse();

            if (!mResponseParser.parseSimpleResponse(prismResponse)) {
                throw new InstrumentException("设置棱镜高失败", "SET_PRISM_ERROR", "TS30");
            }

            // 2. 设置测量模式
            byte[] modeCommand = buildMeasureModeCommand(mode);
            mLinkManager.sendCommand(modeCommand);
            byte[] modeResponse = mLinkManager.receiveResponse();

            if (!mResponseParser.parseSimpleResponse(modeResponse)) {
                throw new InstrumentException("设置测量模式失败", "SET_MODE_ERROR", "TS30");
            }

            // 3. 执行测量
            byte[] measureCommand = mCommandBuilder.buildMeasureCommand(prismHeight, mode);
            mLinkManager.sendCommand(measureCommand);
            byte[] measureResponse = mLinkManager.receiveResponse();

            // 4. 解析测量结果
            return mResponseParser.parseMeasurementResponse(measureResponse);

        } catch (Exception e) {
            throw new InstrumentException("测量失败: " + e.getMessage(), "MEASURE_ERROR", "TS30");
        }
    }

    /**
     * 构建测量模式指令
     */
    private byte[] buildMeasureModeCommand(MeasureMode mode) {
        switch (mode) {
            case PRECISION:
                return mCommandBuilder.buildSetPrecisionModeCommand();
            case FAST:
                return mCommandBuilder.buildSetFastModeCommand();
            case STANDARD:
            default:
                return mCommandBuilder.buildSetStandardModeCommand();
        }
    }
}
