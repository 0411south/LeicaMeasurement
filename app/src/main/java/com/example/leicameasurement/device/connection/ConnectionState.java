package com.example.leicameasurement.device.connection;

public enum ConnectionState {
    DISCONNECTED,    // 未连接
    CONNECTING,      // 连接中
    CONNECTED,       // 已连接
    DISCONNECTING,   // 断开中
    ERROR           // 错误状态
}