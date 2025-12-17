package com.example.leicameasurement.device.bluetooth;

import com.example.leicameasurement.infrastructure.LogManager;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
* 连接监控器：负责心跳检测与断连事件通知
* 核心职责：定时发送心跳指令，检测链路是否正常
*/
public class ConnectionMonitor {

    private static final String TAG = "ConnectionMonitor";
    private static final long HEARTBEAT_INTERVAL = 5000; // 心跳间隔（5秒）
    private static final String HEARTBEAT_COMMAND = "*IDN?\r\n"; // 身份识别指令作为心跳包

    private final BluetoothChannel mChannel;
    private final Runnable mOnDisconnectCallback; // 断连回调
    private ScheduledExecutorService mScheduler; // 定时任务执行器
    private volatile boolean isMonitoring = false;

    public ConnectionMonitor(BluetoothChannel channel, Runnable onDisconnectCallback) {
        this.mChannel = channel;
        this.mOnDisconnectCallback = onDisconnectCallback;
    }

    /**
    * 启动心跳监控
    */
    public void start() {
        if (isMonitoring) return;
        isMonitoring = true;
        mScheduler = Executors.newSingleThreadScheduledExecutor();
        mScheduler.scheduleAtFixedRate(this::checkConnection, 0, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
        LogManager.i(TAG, "连接监控已启动");
    }

    /**
    * 停止心跳监控
    */
    public void stop() {
        isMonitoring = false;
        if (mScheduler != null && !mScheduler.isShutdown()) {
            mScheduler.shutdownNow();
        }
        LogManager.i(TAG, "连接监控已停止");
    }

    /**
    * 检查连接状态
    */
    private void checkConnection() {
        if (!mChannel.isConnected()) {
            LogManager.w(TAG, "心跳检测失败：蓝牙未连接");
            if (isMonitoring) { // 避免停止后重复回调
                mOnDisconnectCallback.run(); // 触发断连回调
            }
            return;
        }
        // 发送心跳指令，但不关心响应（仅用于检测通道是否可用）
        if (!mChannel.sendCommand(HEARTBEAT_COMMAND)) {
            LogManager.e(TAG, "心跳发送失败");
            if (isMonitoring) {
                mOnDisconnectCallback.run();
            }
        }
    }
}