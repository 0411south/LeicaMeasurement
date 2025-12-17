package com.example.leicameasurement.device.connection;

/**
 * 连接异常类
 */
public class ConnectionException extends Exception {

    private final String errorCode;
    private final ConnectionType connectionType;

    public ConnectionException(String message) {
        super(message);
        this.errorCode = "UNKNOWN";
        this.connectionType = ConnectionType.BLUETOOTH;
    }

    public ConnectionException(String message, String errorCode, ConnectionType connectionType) {
        super(message);
        this.errorCode = errorCode;
        this.connectionType = connectionType;
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "UNKNOWN";
        this.connectionType = ConnectionType.BLUETOOTH;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public ConnectionType getConnectionType() {
        return connectionType;
    }
}