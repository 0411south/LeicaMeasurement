package com.example.leicameasurement.device.bluetooth;

import com.example.leicameasurement.infrastructure.LogManager;

/**
* 自动重连策略：负责在连接断开后，按指定策略尝试恢复连接
* 核心职责：实现指数退避重连算法
*/
public class AutoReconnectStrategy {

    private static final String TAG = "AutoReconnectStrategy";
    private static final long INITIAL_BACKOFF_MS = 1000; // 初始重连间隔1s
    private static final long MAX_BACKOFF_MS = 30000;    // 最大重连间隔30s
    private static final int MAX_ATTEMPTS = 10;          // 最大重连次数

    private final BluetoothLinkManager mLinkManager;
    private int mAttemptCount = 0;
    private long mCurrentBackoffMs = INITIAL_BACKOFF_MS;
    private volatile boolean isReconnecting = false;

    public AutoReconnectStrategy(BluetoothLinkManager linkManager) {
        this.mLinkManager = linkManager;
    }

    /**
    * 开始重连
    */
    public synchronized void start() {
        if (isReconnecting) return;
        isReconnecting = true;
        mAttemptCount = 0;
        mCurrentBackoffMs = INITIAL_BACKOFF_MS;
        LogManager.i(TAG, "启动自动重连");
        scheduleNextAttempt();
    }

    /**
    * 停止重连
    */
    public synchronized void stop() {
        isReconnecting = false;
        LogManager.i(TAG, "停止自动重连");
    }

    /**
    * 安排下一次重连尝试
    */
    private void scheduleNextAttempt() {
        if (!isReconnecting || mAttemptCount >= MAX_ATTEMPTS) {
            LogManager.w(TAG, "重连失败：已达最大次数或已停止");
            stop();
            return;
        }

        mAttemptCount++;
        LogManager.d(TAG, "准备第 " + mAttemptCount + " 次重连，延迟 " + mCurrentBackoffMs + "ms");

        new Thread(() -> {
            try {
                Thread.sleep(mCurrentBackoffMs);
                if (!isReconnecting) return; // 检查是否已停止

                mLinkManager.connect(); // 尝试连接

                // 检查连接结果
                if (mLinkManager.getState() == BluetoothLinkManager.LinkState.CONNECTED) {
                    LogManager.i(TAG, "重连成功！");
                    stop(); // 成功后停止重连
                } else {
                    // 连接失败，增加退避时间并安排下一次
                    mCurrentBackoffMs = Math.min(mCurrentBackoffMs * 2, MAX_BACKOFF_MS);
                    scheduleNextAttempt();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                stop();
            }
        }).start();
    }
}