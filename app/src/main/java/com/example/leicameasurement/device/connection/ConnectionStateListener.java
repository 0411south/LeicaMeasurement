package com.example.leicameasurement.device.connection;

/**
 * 连接状态监听器接口
 */
public interface ConnectionStateListener {

    /**
     * 连接状态变化回调
     * @param oldState 旧状态
     * @param newState 新状态
     * @param error 错误信息（如果有）
     */
    void onConnectionStateChanged(IConnectionChannel.ConnectionState oldState,
                                  IConnectionChannel.ConnectionState newState,
                                  String error);

    /**
     * 连接成功回调
     */
    void onConnected();

    /**
     * 连接断开回调
     * @param reason 断开原因
     */
    void onDisconnected(String reason);

    /**
     * 连接错误回调
     * @param error 错误信息
     */
    void onConnectionError(String error);
}