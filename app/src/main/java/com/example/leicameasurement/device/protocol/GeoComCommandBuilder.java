package com.example.leicameasurement.device.protocol;

import com.example.leicameasurement.device.adapter.InstrumentAdapter;

/**
 * GeoCOM指令构建器：负责将高层操作转换为具体的仪器指令
 * 核心职责：封装指令格式，处理参数转换
 */
public class GeoComCommandBuilder {

    /**
     * 构建设置测站坐标指令（不含仪器高）
     * @param stationX X坐标
     * @param stationY Y坐标
     * @param stationH H坐标
     * @return GeoCOM指令字节数组
     */
    public byte[] buildSetStationCommand(double stationX, double stationY, double stationH) {
        // 格式：%R1Q,2114:stationX,stationY,stationH
        String command = String.format(java.util.Locale.US, "%%R1Q,2114:%.4f,%.4f,%.4f",
                stationX, stationY, stationH);
        return (command + ProtocolConstants.COMMAND_TERMINATOR).getBytes();
    }

    /**
     * 构建设置仪器高指令
     * @param instrumentHeight 仪器高
     * @return GeoCOM指令字节数组
     */
    public byte[] buildSetInstrumentHeightCommand(double instrumentHeight) {
        // 格式：%R1Q,2117:instrumentHeight
        String command = String.format(java.util.Locale.US, "%%R1Q,2117:%.4f", instrumentHeight);
        return (command + ProtocolConstants.COMMAND_TERMINATOR).getBytes();
    }

    /**
     * 构建设置棱镜高指令
     * @param prismHeight 棱镜高
     * @return GeoCOM指令字节数组
     */
    public byte[] buildSetPrismHeightCommand(double prismHeight) {
        // 格式：%R1Q,2116:prismHeight
        String command = String.format(java.util.Locale.US, "%%R1Q,2116:%.4f", prismHeight);
        return (command + ProtocolConstants.COMMAND_TERMINATOR).getBytes();
    }

    /**
     * 构建精密测量模式指令
     * @return GeoCOM指令字节数组
     */
    public byte[] buildSetPrecisionModeCommand() {
        // 格式：%R1Q,2008:2,0 (模式2=精密测量)
        String command = "%R1Q,2008:2,0" + ProtocolConstants.COMMAND_TERMINATOR;
        return command.getBytes();
    }

    /**
     * 构建标准测量模式指令
     * @return GeoCOM指令字节数组
     */
    public byte[] buildSetStandardModeCommand() {
        // 格式：%R1Q,2008:1,0 (模式1=标准测量)
        String command = "%R1Q,2008:1,0" + ProtocolConstants.COMMAND_TERMINATOR;
        return command.getBytes();
    }

    /**
     * 构建快速测量模式指令
     * @return GeoCOM指令字节数组
     */
    public byte[] buildSetFastModeCommand() {
        // 格式：%R1Q,2008:3,0 (模式3=快速测量)
        String command = "%R1Q,2008:3,0" + ProtocolConstants.COMMAND_TERMINATOR;
        return command.getBytes();
    }

    /**
     * 构建后视测量指令
     * @return GeoCOM指令字节数组
     */
    public byte[] buildBacksightMeasurementCommand() {
        // 格式：%R1Q,17008:0 (后视测量)
        String command = "%R1Q,17008:0" + ProtocolConstants.COMMAND_TERMINATOR;
        return command.getBytes();
    }

    /**
     * 构建前视测量指令
     * @return GeoCOM指令字节数组
     */
    public byte[] buildForepointMeasurementCommand() {
        // 格式：%R1Q,17008:1 (前视测量)
        String command = "%R1Q,17008:1" + ProtocolConstants.COMMAND_TERMINATOR;
        return command.getBytes();
    }

    /**
     * 构建碎步点测量指令
     * @return GeoCOM指令字节数组
     */
    public byte[] buildDetailPointMeasurementCommand() {
        // 格式：%R1Q,17008:2 (碎步点测量)
        String command = "%R1Q,17008:2" + ProtocolConstants.COMMAND_TERMINATOR;
        return command.getBytes();
    }

    /**
     * 构建获取仪器型号指令
     * @return GeoCOM指令字节数组
     */
    public byte[] buildGetModelCommand() {
        // 格式：%R1Q,5003:
        String command = "%R1Q,5003:" + ProtocolConstants.COMMAND_TERMINATOR;
        return command.getBytes();
    }

    /**
     * 构建获取序列号指令
     * @return GeoCOM指令字节数组
     */
    public byte[] buildGetSerialNumberCommand() {
        // 格式：%R1Q,5004:
        String command = "%R1Q,5004:" + ProtocolConstants.COMMAND_TERMINATOR;
        return command.getBytes();
    }

    /**
     * 构建获取仪器状态指令
     * @return GeoCOM指令字节数组
     */
    public byte[] buildGetStatusCommand() {
        // 格式：%R1Q,5002:
        String command = "%R1Q,5002:" + ProtocolConstants.COMMAND_TERMINATOR;
        return command.getBytes();
    }

    /**
     * 构建关闭仪器连接指令
     * @return GeoCOM指令字节数组
     */
    public byte[] buildCloseCommand() {
        // 格式：%R1Q,9002:
        String command = "%R1Q,9002:" + ProtocolConstants.COMMAND_TERMINATOR;
        return command.getBytes();
    }

    /**
     * 构建通用测量指令（保持向后兼容）
     * @param prismHeight 棱镜高
     * @param mode 测量模式
     * @return GeoCOM指令字节数组
     */
    public byte[] buildMeasureCommand(double prismHeight, InstrumentAdapter.MeasureMode mode) {
        int geoComMode = 1; // 默认为标准模式
        if (mode == InstrumentAdapter.MeasureMode.PRECISION) {
            geoComMode = 2;
        } else if (mode == InstrumentAdapter.MeasureMode.FAST) {
            geoComMode = 3;
        }
        String command = String.format(java.util.Locale.US, "%%R1Q,2008:%d,%.4f", geoComMode, prismHeight);
        return (command + ProtocolConstants.COMMAND_TERMINATOR).getBytes();
    }

    /**
     * 构建停止测量指令
     * @return GeoCOM指令字节数组
     */
    public byte[] buildStopMeasureCommand() {
        return ProtocolConstants.CMD_STOP_MEASURE.getBytes();
    }

    /**
     * 构建身份识别指令（用于心跳）
     * @return GeoCOM指令字节数组
     */
    public byte[] buildIdentificationCommand() {
        return ("*IDN?" + ProtocolConstants.COMMAND_TERMINATOR).getBytes();
    }

    /**
     * 构建改变仪器状态指令（锁定/解锁）
     * @param lock true=锁定，false=解锁
     * @return GeoCOM指令字节数组
     */
    public byte[] buildChangeInstrumentStateCommand(boolean lock) {
        // 格式：%R1Q,9007:state (0=解锁, 1=锁定)
        int state = lock ? 1 : 0;
        String command = String.format("%%R1Q,9007:%d", state) + ProtocolConstants.COMMAND_TERMINATOR;
        return command.getBytes();
    }

    /**
     * 构建获取电池状态指令
     * @return GeoCOM指令字节数组
     */
    public byte[] buildGetBatteryStatusCommand() {
        // 格式：%R1Q,5005:
        String command = "%R1Q,5005:" + ProtocolConstants.COMMAND_TERMINATOR;
        return command.getBytes();
    }
}