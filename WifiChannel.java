package com.example.leicameasurement.device.wifi;

import com.example.leicameasurement.device.connection.IConnectionChannel;
import com.example.leicameasurement.device.connection.ConnectionConfig;
import com.example.leicameasurement.device.connection.ConnectionException;
import com.example.leicameasurement.device.connection.ConnectionState;
import com.example.leicameasurement.device.connection.ConnectionStateListener;
import com.example.leicameasurement.device.connection.ConnectionType; // 添加这行导入

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * WiFi通道实现：通过TCP Socket连接全站仪
 */
public class WifiChannel implements IConnectionChannel {

    private Socket socket;
    private String ipAddress;
    private int port;
    private ConnectionState state = ConnectionState.DISCONNECTED;
    private WifiConfig wifiConfig;
    private int connectionTimeout = 10000; // 10秒连接超时
    private int readTimeout = 5000; // 5秒读取超时

    private final List<ConnectionStateListener> listeners = new CopyOnWriteArrayList<>();

    @Override
    public void connect(ConnectionConfig config) throws ConnectionException {
        if (!(config instanceof WifiConfig)) {
            throw new ConnectionException("配置类型错误，需要WifiConfig");
        }

        this.wifiConfig = (WifiConfig) config;
        this.ipAddress = wifiConfig.getIpAddress();
        this.port = wifiConfig.getPort();

        // 通知状态变化
        notifyStateChange(ConnectionState.CONNECTING);

        try {
            // 创建Socket并设置连接超时
            socket = new Socket();
            java.net.InetSocketAddress address = new java.net.InetSocketAddress(ipAddress, port);
            socket.connect(address, connectionTimeout);
            socket.setSoTimeout(readTimeout);

            // 连接成功
            this.state = ConnectionState.CONNECTED;
            notifyStateChange(ConnectionState.CONNECTED);

        } catch (IOException e) {
            this.state = ConnectionState.ERROR;
            String errorMsg = "WiFi连接失败: " + e.getMessage() + " [" + ipAddress + ":" + port + "]";
            notifyStateChange(ConnectionState.ERROR, errorMsg);
            throw new ConnectionException(errorMsg, "WIFI_CONNECT_ERROR", ConnectionType.WIFI);
        }
    }

    @Override
    public void disconnect() {
        if (socket != null && !socket.isClosed()) {
            try {
                state = ConnectionState.DISCONNECTING;
                notifyStateChange(ConnectionState.DISCONNECTING);

                socket.close();
            } catch (IOException e) {
                // 忽略关闭时的异常
            } finally {
                socket = null;
                state = ConnectionState.DISCONNECTED;
                notifyStateChange(ConnectionState.DISCONNECTED, "手动断开连接");
            }
        }
    }

    @Override
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    @Override
    public ConnectionState getConnectionState() {
        return state;
    }

    @Override
    public void sendData(byte[] data) throws IOException {
        if (!isConnected()) {
            throw new IOException("WiFi未连接，无法发送数据");
        }

        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            state = ConnectionState.ERROR;
            notifyStateChange(ConnectionState.ERROR, "发送数据失败: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public byte[] receiveData() throws IOException {
        if (!isConnected()) {
            throw new IOException("WiFi未连接，无法接收数据");
        }

        try {
            InputStream inputStream = socket.getInputStream();
            // 简单的读取实现 - 实际可能需要更复杂的协议处理
            List<Byte> byteList = new ArrayList<>();
            int data;
            while ((data = inputStream.read()) != -1) {
                byteList.add((byte) data);
                // 如果遇到协议终止符，停止读取
                if (data == 0x03) { // ETX
                    break;
                }
            }

            // 转换为字节数组
            byte[] result = new byte[byteList.size()];
            for (int i = 0; i < byteList.size(); i++) {
                result[i] = byteList.get(i);
            }
            return result;

        } catch (IOException e) {
            state = ConnectionState.ERROR;
            notifyStateChange(ConnectionState.ERROR, "接收数据失败: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (!isConnected()) {
            throw new IOException("WiFi未连接");
        }
        return socket.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (!isConnected()) {
            throw new IOException("WiFi未连接");
        }
        return socket.getOutputStream();
    }

    @Override
    public ConnectionConfig getConnectionConfig() {
        return wifiConfig;
    }

    @Override
    public void setConnectionTimeout(int timeoutMs) {
        this.connectionTimeout = timeoutMs;
    }

    @Override
    public void setReadTimeout(int timeoutMs) {
        this.readTimeout = timeoutMs;
        if (socket != null && !socket.isClosed()) {
            try {
                socket.setSoTimeout(timeoutMs);
            } catch (IOException e) {
                // 忽略设置超时时的异常
            }
        }
    }

    @Override
    public void addConnectionStateListener(ConnectionStateListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeConnectionStateListener(ConnectionStateListener listener) {
        listeners.remove(listener);
    }

    /**
     * 获取连接类型
     */
    public ConnectionType getConnectionType() {
        return ConnectionType.WIFI;
    }

    /**
     * 通知状态变化
     */
    private void notifyStateChange(ConnectionState newState) {
        notifyStateChange(newState, null);
    }

    /**
     * 通知状态变化
     */
    private void notifyStateChange(ConnectionState newState, String error) {
        ConnectionState oldState = this.state;
        this.state = newState;

        for (ConnectionStateListener listener : listeners) {
            try {
                listener.onConnectionStateChanged(oldState, newState, error);

                if (newState == ConnectionState.CONNECTED) {
                    listener.onConnected();
                } else if (newState == ConnectionState.DISCONNECTED) {
                    listener.onDisconnected(error != null ? error : "正常断开");
                } else if (newState == ConnectionState.ERROR) {
                    listener.onConnectionError(error != null ? error : "未知错误");
                }
            } catch (Exception e) {
                // 防止监听器异常影响主流程
            }
        }
    }

    /**
     * 获取IP地址
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * 获取端口号
     */
    public int getPort() {
        return port;
    }
}