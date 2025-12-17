package com.example.leicameasurement.device.wifi;

import com.example.leicameasurement.device.connection.ConnectionConfig;
import com.example.leicameasurement.device.connection.ConnectionType;

public class WifiConfig extends ConnectionConfig {
    private String ipAddress;
    private int port;

    public WifiConfig(String ipAddress, int port) {
        // 调用父类构造函数，传入 IP 地址和连接类型
        super(ipAddress, ConnectionConfig.ConnectionType.WIFI);
        this.ipAddress = ipAddress;
        this.port = port;
        // 设置端口号
        setPort(port);
    }

    // 修正方法名：getIpAddress() 而不是 getipAddress()
    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public int getPort() {
        return port;
    }
}
