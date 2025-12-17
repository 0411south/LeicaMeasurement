package com.example.leicameasurement.device.wifi;

import com.example.leicameasurement.device.connection.ConnectionState;
import com.example.leicameasurement.device.connection.ConnectionStateListener;
import com.example.leicameasurement.infrastructure.LogManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * WiFi 连接监控器
 * 定期检查连接状态，检测连接断开
 */
public class WifiConnectionMonitor {

    private static final String TAG = "WifiConnectionMonitor";
    private static final long DEFAULT_CHECK_INTERVAL_MS = 5000; // 5秒检查一次
    private static final int HEARTBEAT_TIMEOUT_MS = 3000;

    private WifiChannel wifiChannel;
    private ScheduledExecutorService scheduler;
    private boolean isMonitoring = false;
    private long checkIntervalMs = DEFAULT_CHECK_INTERVAL_MS;

    private final List<ConnectionStateListener> listeners = new CopyOnWriteArrayList<>();

    // ==================== 构造函数 ====================

    public WifiConnectionMonitor(WifiChannel wifiChannel) {
        this.wifiChannel = wifiChannel;
    }

    // ==================== 监控控制 ====================

    /**
     * 开始监控
     */
    public void startMonitoring() {
        if (isMonitoring) {
            LogManager.w(TAG, "Monitoring already started");
            return;
        }

        isMonitoring = true;
        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(
                this::checkConnection,
                checkIntervalMs,
                checkIntervalMs,
                TimeUnit.MILLISECONDS
        );

        LogManager.i(TAG, "Connection monitoring started (interval: " + checkIntervalMs + "ms)");
    }

    /**
     * 停止监控
     */
    public void stopMonitoring() {
        if (!isMonitoring) {
            return;
        }

        isMonitoring = false;

        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
            scheduler = null;
        }

        LogManager.i(TAG, "Connection monitoring stopped");
    }

    /**
     * 设置检查间隔
     */
    public void setCheckInterval(long intervalMs) {
        this.checkIntervalMs = intervalMs;

        // 如果正在监控，重启以应用新间隔
        if (isMonitoring) {
            stopMonitoring();
            startMonitoring();
        }
    }

    // ==================== 连接检查 ====================

    /**
     * 检查连接状态
     */
    private void checkConnection() {
        try {
            if (wifiChannel == null) {
                LogManager.w(TAG, "WiFi channel is null");
                return;
            }

            if (!wifiChannel.isConnected()) {
                LogManager.w(TAG, "WiFi connection lost");
                notifyDisconnected("Connection check failed: not connected");
                return;
            }

            // 发送心跳包检查连接
            if (!sendHeartbeat()) {
                LogManager.w(TAG, "Heartbeat failed");
                notifyConnectionError("Heartbeat timeout");
            }

        } catch (Exception e) {
            LogManager.e(TAG, "Connection check error: " + e.getMessage());
            notifyConnectionError("Connection check error: " + e.getMessage());
        }
    }

    /**
     * 发送心跳包
     */
    private boolean sendHeartbeat() {
        try {
            // 简单的心跳实现：尝试获取输出流
            // 实际应用中可能需要发送特定的心跳命令
            if (wifiChannel.getOutputStream() != null) {
                return true;
            }
            return false;

        } catch (IOException e) {
            LogManager.e(TAG, "Heartbeat failed: " + e.getMessage());
            return false;
        }
    }

    // ==================== 状态监听 ====================

    public void addConnectionStateListener(ConnectionStateListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeConnectionStateListener(ConnectionStateListener listener) {
        listeners.remove(listener);
    }

    private void notifyDisconnected(String reason) {
        for (ConnectionStateListener listener : listeners) {
            try {
                listener.onDisconnected(reason);
            } catch (Exception e) {
                LogManager.e(TAG, "Error notifying listener: " + e.getMessage());
            }
        }
    }

    private void notifyConnectionError(String error) {
        for (ConnectionStateListener listener : listeners) {
            try {
                listener.onConnectionError(error);
            } catch (Exception e) {
                LogManager.e(TAG, "Error notifying listener: " + e.getMessage());
            }
        }
    }

    // ==================== Getter 方法 ====================

    public boolean isMonitoring() {
        return isMonitoring;
    }

    public long getCheckInterval() {
        return checkIntervalMs;
    }
}
