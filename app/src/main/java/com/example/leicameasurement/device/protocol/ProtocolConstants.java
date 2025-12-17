package com.example.leicameasurement.device.protocol;

/**
* GeoCOM协议常量：避免硬编码
*/
public class ProtocolConstants {

    // 指令头（GeoCOM协议要求）
    public static final String COMMAND_HEADER = "*";

    // 指令结束符（CR+LF）
    public static final String COMMAND_TERMINATOR = "\r\n";

    // 常用指令（TS60）
    public static final String CMD_SET_STATION = "GEO::SETSTATION"; // 测站设置
    public static final String CMD_MEASURE = "GEO::MEASURE"; // 测量指令
    public static final String CMD_STOP_MEASURE = "GEO::STOPMEASURE"; // 停止测量

    // 成功响应标识
    public static final String RESPONSE_SUCCESS = "0";

}
