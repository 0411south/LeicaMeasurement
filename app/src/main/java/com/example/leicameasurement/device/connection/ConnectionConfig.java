package com.example.leicameasurement.device.connection;

/**
 * 连接配置类：封装连接所需的配置信息
 */
public class ConnectionConfig {

    private String deviceAddress;  // 设备地址（蓝牙MAC或IP地址）
    private int port;              // 端口号（WiFi使用）
    private int connectionTimeout; // 连接超时时间（毫秒）
    private int readTimeout;       // 读取超时时间（毫秒）
    private ConnectionType type;   // 连接类型

    public enum ConnectionType {
        BLUETOOTH,
        WIFI,
        USB
    }

    public ConnectionConfig(String deviceAddress, ConnectionType type) {
        this.deviceAddress = deviceAddress;
        this.type = type;
        this.connectionTimeout = 5000;  // 默认5秒
        this.readTimeout = 5000;        // 默认5秒
        this.port = 0;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public ConnectionType getType() {
        return type;
    }

    public void setType(ConnectionType type) {
        this.type = type;
    }
}
