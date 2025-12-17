package com.example.leicameasurement.device.wifi;

import com.example.leicameasurement.infrastructure.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * WiFi 数据收发器
 * 负责数据的发送和接收，提供线程安全的数据传输
 */
public class WifiTransceiver {

    private static final String TAG = "WifiTransceiver";
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private static final int DEFAULT_TIMEOUT_MS = 5000;

    private WifiChannel wifiChannel;
    private final Lock sendLock = new ReentrantLock();
    private final Lock receiveLock = new ReentrantLock();

    // ==================== 构造函数 ====================

    public WifiTransceiver(WifiChannel wifiChannel) {
        this.wifiChannel = wifiChannel;
    }

    // ==================== 数据发送 ====================

    /**
     * 发送数据（线程安全）
     */
    public void sendData(byte[] data) throws IOException {
        if (data == null || data.length == 0) {
            throw new IOException("Data is null or empty");
        }

        sendLock.lock();
        try {
            if (!wifiChannel.isConnected()) {
                throw new IOException("WiFi not connected");
            }

            wifiChannel.sendData(data);
            LogManager.d(TAG, "Sent " + data.length + " bytes: " + bytesToHex(data));

        } finally {
            sendLock.unlock();
        }
    }

    /**
     * 发送字符串数据
     */
    public void sendString(String data) throws IOException {
        if (data == null || data.isEmpty()) {
            throw new IOException("String data is null or empty");
        }
        sendData(data.getBytes("UTF-8"));
    }

    // ==================== 数据接收 ====================

    /**
     * 接收数据（线程安全）
     */
    public byte[] receiveData() throws IOException {
        return receiveData(DEFAULT_TIMEOUT_MS);
    }

    /**
     * 接收数据（指定超时）
     */
    public byte[] receiveData(int timeoutMs) throws IOException {
        receiveLock.lock();
        try {
            if (!wifiChannel.isConnected()) {
                throw new IOException("WiFi not connected");
            }

            byte[] data = wifiChannel.receiveData();
            LogManager.d(TAG, "Received " + data.length + " bytes: " + bytesToHex(data));
            return data;

        } finally {
            receiveLock.unlock();
        }
    }

    /**
     * 接收字符串数据
     */
    public String receiveString() throws IOException {
        return receiveString(DEFAULT_TIMEOUT_MS);
    }

    /**
     * 接收字符串数据（指定超时）
     */
    public String receiveString(int timeoutMs) throws IOException {
        byte[] data = receiveData(timeoutMs);
        return new String(data, "UTF-8");
    }

    // ==================== 命令收发 ====================

    /**
     * 发送命令并接收响应
     */
    public byte[] sendCommandAndReceive(byte[] command) throws IOException {
        return sendCommandAndReceive(command, DEFAULT_TIMEOUT_MS);
    }

    /**
     * 发送命令并接收响应（指定超时）
     */
    public byte[] sendCommandAndReceive(byte[] command, int timeoutMs) throws IOException {
        sendLock.lock();
        receiveLock.lock();
        try {
            // 发送命令
            sendData(command);

            // 等待响应
            Thread.sleep(100); // 短暂延迟，等待设备处理

            // 接收响应
            return receiveData(timeoutMs);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Command interrupted: " + e.getMessage());
        } finally {
            receiveLock.unlock();
            sendLock.unlock();
        }
    }

    /**
     * 发送字符串命令并接收字符串响应
     */
    public String sendCommandAndReceiveString(String command) throws IOException {
        return sendCommandAndReceiveString(command, DEFAULT_TIMEOUT_MS);
    }

    /**
     * 发送字符串命令并接收字符串响应（指定超时）
     */
    public String sendCommandAndReceiveString(String command, int timeoutMs) throws IOException {
        byte[] response = sendCommandAndReceive(command.getBytes("UTF-8"), timeoutMs);
        return new String(response, "UTF-8");
    }

    // ==================== 流式操作 ====================

    /**
     * 获取输入流
     */
    public InputStream getInputStream() throws IOException {
        return wifiChannel.getInputStream();
    }

    /**
     * 获取输出流
     */
    public OutputStream getOutputStream() throws IOException {
        return wifiChannel.getOutputStream();
    }

    // ==================== 辅助方法 ====================

    /**
     * 字节数组转十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }

        // 只显示前32字节
        int length = Math.min(bytes.length, 32);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(String.format("%02X ", bytes[i]));
        }
        if (bytes.length > 32) {
            sb.append("... (").append(bytes.length).append(" bytes total)");
        }
        return sb.toString().trim();
    }

    /**
     * 清空输入缓冲区
     */
    public void clearInputBuffer() throws IOException {
        InputStream inputStream = getInputStream();
        while (inputStream.available() > 0) {
            inputStream.read();
        }
        LogManager.d(TAG, "Input buffer cleared");
    }

    // ==================== Getter 方法 ====================

    public WifiChannel getWifiChannel() {
        return wifiChannel;
    }
}
