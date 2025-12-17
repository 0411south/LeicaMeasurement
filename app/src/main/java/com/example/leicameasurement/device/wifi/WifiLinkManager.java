package com.example.leicameasurement.device.wifi;

import com.example.leicameasurement.device.connection.ConnectionException;
import com.example.leicameasurement.device.connection.ConnectionStateListener;
import com.example.leicameasurement.device.connection.IConnectionChannel;
import com.example.leicameasurement.infrastructure.LogManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * WiFi 链路管理器
 * 负责管理 WiFi 连接的生命周期、状态监控和自动重连
 * 类似于 BluetoothLinkManager 的功能
 */
public class WifiLinkManager implements ConnectionStateListener {

    private static final String TAG = "WifiLinkManager";

    private WifiChannel wifiChannel;
    private WifiConfig wifiConfig;
    private WifiConnectionMonitor connectionMonitor;
    private WifiTransceiver transceiver;

    private IConnectionChannel.ConnectionState currentState = IConnectionChannel.ConnectionState.DISCONNECTED;
    private boolean autoReconnectEnabled = false;
    private int reconnectAttempts = 0;
    private int maxReconnectAttempts = 3;
    private long reconnectDelayMs = 2000;

    private final List<ConnectionStateListener> stateListeners = new CopyOnWriteArrayList<>();

    // ==================== 构造函数 ====================

    public WifiLinkManager() {
    }

    public WifiLinkManager(WifiConfig config) {
        this.wifiConfig = config;
        initialize();
    }

    // ==================== 初始化 ====================

    private void initialize() {
        // 创建 WiFi 通道
        wifiChannel = new WifiChannel();
        wifiChannel.addConnectionStateListener(this);

        // 创建连接监控器
        connectionMonitor = new WifiConnectionMonitor(wifiChannel);
        connectionMonitor.addConnectionStateListener(this);

        // 创建数据收发器
        transceiver = new WifiTransceiver(wifiChannel);

        LogManager.i(TAG, "WifiLinkManager initialized");
    }

    // ==================== 连接管理 ====================

    /**
     * 连接到 WiFi 设备
     */
    public void connect(WifiConfig config) throws ConnectionException {
        this.wifiConfig = config;

        if (wifiChannel == null) {
            initialize();
        }

        LogManager.i(TAG, "Connecting to WiFi device: " + config.getIpAddress() + ":" + config.getPort());

        try {
            wifiChannel.connect(config);
            reconnectAttempts = 0;

            // 启动连接监控
            if (connectionMonitor != null) {
                connectionMonitor.startMonitoring();
            }

        } catch (ConnectionException e) {
            LogManager.e(TAG, "WiFi connection failed: " + e.getMessage());
            handleConnectionFailure(e);
            throw e;
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        LogManager.i(TAG, "Disconnecting WiFi");

        autoReconnectEnabled = false;

        // 停止连接监控
        if (connectionMonitor != null) {
            connectionMonitor.stopMonitoring();
        }

        // 断开通道
        if (wifiChannel != null) {
            wifiChannel.disconnect();
        }

        reconnectAttempts = 0;
    }

    /**
     * 检查是否已连接
     */
    public boolean isConnected() {
        return wifiChannel != null && wifiChannel.isConnected();
    }

    /**
     * 获取连接状态
     */
    public IConnectionChannel.ConnectionState getConnectionState() {
        return currentState;
    }

    // ==================== 自动重连 ====================

    /**
     * 启用自动重连
     */
    public void enableAutoReconnect(boolean enable) {
        this.autoReconnectEnabled = enable;
        LogManager.i(TAG, "Auto reconnect " + (enable ? "enabled" : "disabled"));
    }

    /**
     * 设置最大重连次数
     */
    public void setMaxReconnectAttempts(int maxAttempts) {
        this.maxReconnectAttempts = maxAttempts;
    }

    /**
     * 设置重连延迟
     */
    public void setReconnectDelay(long delayMs) {
        this.reconnectDelayMs = delayMs;
    }

    /**
     * 尝试重连
     */
    private void attemptReconnect() {
        if (!autoReconnectEnabled || wifiConfig == null) {
            return;
        }

        if (reconnectAttempts >= maxReconnectAttempts) {
            LogManager.w(TAG, "Max reconnect attempts reached (" + maxReconnectAttempts + ")");
            notifyConnectionError("重连失败：已达到最大重连次数");
            return;
        }

        reconnectAttempts++;
        LogManager.i(TAG, "Attempting reconnect " + reconnectAttempts + "/" + maxReconnectAttempts);

        new Thread(() -> {
            try {
                Thread.sleep(reconnectDelayMs);
                connect(wifiConfig);
            } catch (Exception e) {
                LogManager.e(TAG, "Reconnect failed: " + e.getMessage());
                attemptReconnect();
            }
        }).start();
    }

    // ==================== 数据收发 ====================

    /**
     * 发送数据
     */
    public void sendData(byte[] data) throws IOException {
        if (transceiver == null) {
            throw new IOException("Transceiver not initialized");
        }
        transceiver.sendData(data);
    }

    /**
     * 接收数据
     */
    public byte[] receiveData() throws IOException {
        if (transceiver == null) {
            throw new IOException("Transceiver not initialized");
        }
        return transceiver.receiveData();
    }

    /**
     * 发送命令并接收响应
     */
    public byte[] sendCommandAndReceive(byte[] command) throws IOException {
        if (transceiver == null) {
            throw new IOException("Transceiver not initialized");
        }
        return transceiver.sendCommandAndReceive(command);
    }

    // ==================== 状态监听 ====================

    @Override
    public void onConnected() {
        LogManager.i(TAG, "WiFi connected");
        currentState = IConnectionChannel.ConnectionState.CONNECTED;
        reconnectAttempts = 0;
        notifyConnected();
    }

    @Override
    public void onDisconnected(String reason) {
        LogManager.i(TAG, "WiFi disconnected: " + reason);
        currentState = IConnectionChannel.ConnectionState.DISCONNECTED;
        notifyDisconnected(reason);

        // 尝试自动重连
        if (autoReconnectEnabled) {
            attemptReconnect();
        }
    }

    @Override
    public void onConnectionError(String error) {
        LogManager.e(TAG, "WiFi connection error: " + error);
        currentState = IConnectionChannel.ConnectionState.ERROR;
        notifyConnectionError(error);
    }

    @Override
    public void onConnectionStateChanged(IConnectionChannel.ConnectionState oldState,
                                         IConnectionChannel.ConnectionState newState,
                                         String error) {
        LogManager.d(TAG, "State changed: " + oldState + " -> " + newState);
        currentState = newState;
        notifyStateChanged(oldState, newState, error);
    }

    /**
     * 添加状态监听器
     */
    public void addConnectionStateListener(ConnectionStateListener listener) {
        if (listener != null && !stateListeners.contains(listener)) {
            stateListeners.add(listener);
        }
    }

    /**
     * 移除状态监听器
     */
    public void removeConnectionStateListener(ConnectionStateListener listener) {
        stateListeners.remove(listener);
    }

    // ==================== 私有方法 ====================

    private void handleConnectionFailure(ConnectionException e) {
        if (autoReconnectEnabled) {
            attemptReconnect();
        }
    }

    private void notifyConnected() {
        for (ConnectionStateListener listener : stateListeners) {
            try {
                listener.onConnected();
            } catch (Exception e) {
                LogManager.e(TAG, "Error notifying listener: " + e.getMessage());
            }
        }
    }

    private void notifyDisconnected(String reason) {
        for (ConnectionStateListener listener : stateListeners) {
            try {
                listener.onDisconnected(reason);
            } catch (Exception e) {
                LogManager.e(TAG, "Error notifying listener: " + e.getMessage());
            }
        }
    }

    private void notifyConnectionError(String error) {
        for (ConnectionStateListener listener : stateListeners) {
            try {
                listener.onConnectionError(error);
            } catch (Exception e) {
                LogManager.e(TAG, "Error notifying listener: " + e.getMessage());
            }
        }
    }

    private void notifyStateChanged(IConnectionChannel.ConnectionState oldState,
                                    IConnectionChannel.ConnectionState newState,
                                    String error) {
        for (ConnectionStateListener listener : stateListeners) {
            try {
                listener.onConnectionStateChanged(oldState, newState, error);
            } catch (Exception e) {
                LogManager.e(TAG, "Error notifying listener: " + e.getMessage());
            }
        }
    }

    // ==================== Getter 方法 ====================

    public WifiChannel getWifiChannel() {
        return wifiChannel;
    }

    public WifiConfig getWifiConfig() {
        return wifiConfig;
    }

    public WifiTransceiver getTransceiver() {
        return transceiver;
    }

    public int getReconnectAttempts() {
        return reconnectAttempts;
    }

    /**
     * 释放资源
     */
    public void release() {
        disconnect();

        if (connectionMonitor != null) {
            connectionMonitor.stopMonitoring();
        }

        stateListeners.clear();

        LogManager.i(TAG, "WifiLinkManager released");
    }
}
