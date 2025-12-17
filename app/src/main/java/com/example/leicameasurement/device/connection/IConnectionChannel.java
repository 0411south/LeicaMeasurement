package com.example.leicameasurement.device.connection;

import java.io.IOException;

/**
 * 连接通道接口：定义统一的连接操作标准
 * 核心职责：抽象蓝牙和WiFi的连接操作，实现技术无关性
 */
public interface IConnectionChannel {

    /**
     * 连接状态枚举
     */
    enum ConnectionState {
        DISCONNECTED,    // 未连接
        CONNECTING,      // 连接中
        CONNECTED,       // 已连接
        DISCONNECTING,   // 断开中
        ERROR           // 错误状态
    }

    /**
     * 建立连接
     * @param config 连接配置
     * @throws ConnectionException 连接异常
     */
    void connect(ConnectionConfig config) throws ConnectionException;

    /**
     * 断开连接
     */
    void disconnect();

    /**
     * 检查连接状态
     * @return true=已连接
     */
    boolean isConnected();

    /**
     * 获取详细的连接状态
     * @return 连接状态枚举
     */
    ConnectionState getConnectionState();

    /**
     * 发送数据
     * @param data 要发送的数据字节数组
     * @throws IOException 发送异常
     */
    void sendData(byte[] data) throws IOException;

    /**
     * 接收数据
     * @return 接收到的数据字节数组
     * @throws IOException 接收异常
     */
    byte[] receiveData() throws IOException;

    /**
     * 获取输入流（用于需要流式读写的场景）
     * @return 输入流
     * @throws IOException 流获取异常
     */
    java.io.InputStream getInputStream() throws IOException;

    /**
     * 获取输出流（用于需要流式读写的场景）
     * @return 输出流
     * @throws IOException 流获取异常
     */
    java.io.OutputStream getOutputStream() throws IOException;

    /**
     * 获取连接配置信息
     * @return 连接配置
     */
    ConnectionConfig getConnectionConfig();

    /**
     * 设置连接超时时间
     * @param timeoutMs 超时时间（毫秒）
     */
    void setConnectionTimeout(int timeoutMs);

    /**
     * 设置读取超时时间
     * @param timeoutMs 超时时间（毫秒）
     */
    void setReadTimeout(int timeoutMs);

    /**
     * 添加连接状态监听器
     * @param listener 状态监听器
     */
    void addConnectionStateListener(ConnectionStateListener listener);

    /**
     * 移除连接状态监听器
     * @param listener 状态监听器
     */
    void removeConnectionStateListener(ConnectionStateListener listener);
}
