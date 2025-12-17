package com.example.leicameasurement.device.bluetooth;

import com.example.leicameasurement.infrastructure.LogManager;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
* 数据收发器：负责指令的队列发送与响应的同步接收
* 核心职责：解决 “一发一收” 的同步问题，处理超时与重发
*/
public class DataTransceiver {

    private static final String TAG = "DataTransceiver";
    private final BluetoothChannel mChannel; // 蓝牙物理通道
    private final BlockingQueue<String> mCommandQueue = new LinkedBlockingQueue<>(); // 指令发送队列
    private volatile boolean isRunning = false; // 运行状态
    private Thread mTransceiverThread; // 收发线程

    public DataTransceiver(BluetoothChannel channel) {
        this.mChannel = channel;
    }

    /**
    * 启动收发器（在连接成功后调用）
    */
    public void start() {
        if (isRunning) return;
        isRunning = true;
        mTransceiverThread = new Thread(this::transceiverLoop);
        mTransceiverThread.start();
        LogManager.i(TAG, "数据收发器已启动");
    }

    /**
    * 停止收发器（在连接断开后调用）
    */
    public void stop() {
        isRunning = false;
        if (mTransceiverThread != null) {
            mTransceiverThread.interrupt(); // 中断线程
        }
        mCommandQueue.clear(); // 清空队列
        LogManager.i(TAG, "数据收发器已停止");
    }

    /**
    * 发送指令并等待响应（同步阻塞）
    * @param command 指令
    * @param timeout 超时时间（ms）
    * @return 响应数据（null=超时或失败）
    */
    public synchronized String sendAndReceive(String command, long timeout) {
        if (!mChannel.isConnected()) {
            LogManager.e(TAG, "发送失败：蓝牙未连接");
            return null;
        }
        try {
            // 1. 发送指令
            if (!mChannel.sendCommand(command)) {
                return null;
            }
            // 2. 等待响应（阻塞）
            return mChannel.receiveResponse(timeout);
        } catch (Exception e) {
            LogManager.e(TAG, "收发异常：" + e.getMessage());
            return null;
        }
    }

    /**
    * 仅发送指令（异步，不关心响应）
    * @param command 指令
    */
    public void sendCommand(String command) {
        try {
            mCommandQueue.put(command);
        } catch (InterruptedException e) {
            LogManager.e(TAG, "添加指令到队列失败：" + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
    * 收发循环（用于处理队列中的异步指令）
    */
    private void transceiverLoop() {
        while (isRunning && !Thread.currentThread().isInterrupted()) {
            try {
                // 从队列中取出指令（阻塞）
                String command = mCommandQueue.poll(1, TimeUnit.SECONDS);
                if (command != null && mChannel.isConnected()) {
                    mChannel.sendCommand(command); // 发送
                    // 对于异步指令，我们不处理响应，仅记录日志
                    LogManager.d(TAG, "已发送异步指令：" + command.trim());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}