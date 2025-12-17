package com.example.leicameasurement.device.protocol;

/**
* 仪器错误码：映射徕卡返回的错误码，便于前端显示
*/
public enum ErrorCodes {

    INVALID_COMMAND(0x01, "无效指令，请检查指令格式"),
    TARGET_TOO_FAR(0x05, "目标太远，无法测量"),
    SIGNAL_TOO_WEAK(0x08, "信号太弱，请调整目标"),
    DEVICE_BUSY(0x0A, "仪器忙，请稍后重试"),
    UNKNOWN_ERROR(0xFF, "未知错误");

    private final int code;
    private final String message;

    ErrorCodes(int code, String message) {
        this.code = code;
        this.message = message;
    }

    // 根据错误码获取描述
    public static String getMessageByCode(int code) {
        for (ErrorCodes error : values()) {
            if (error.code == code) {
                return error.message;
            }
        }
        return UNKNOWN_ERROR.message;
    }
}
