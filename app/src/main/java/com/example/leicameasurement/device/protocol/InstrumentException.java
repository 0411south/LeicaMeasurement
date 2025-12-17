package com.example.leicameasurement.device.protocol;

/**
 * 仪器操作异常
 */
public class InstrumentException extends Exception {
    private final String errorCode;
    private final String instrumentModel;

    public InstrumentException(String message) {
        super(message);
        this.errorCode = "UNKNOWN";
        this.instrumentModel = "UNKNOWN";
    }

    public InstrumentException(String message, String errorCode, String instrumentModel) {
        super(message);
        this.errorCode = errorCode;
        this.instrumentModel = instrumentModel;
    }

    public InstrumentException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "UNKNOWN";
        this.instrumentModel = "UNKNOWN";
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getInstrumentModel() {
        return instrumentModel;
    }
}