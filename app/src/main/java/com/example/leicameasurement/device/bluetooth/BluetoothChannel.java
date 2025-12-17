package com.example.leicameasurement.device.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import com.example.leicameasurement.device.connection.ConnectionConfig;
import com.example.leicameasurement.device.connection.ConnectionException;
import com.example.leicameasurement.device.connection.ConnectionStateListener;
import com.example.leicameasurement.device.connection.ConnectionType;
import com.example.leicameasurement.device.connection.IConnectionChannel;
import com.example.leicameasurement.infrastructure.LogManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 蓝牙通道：负责底层的蓝牙连接、数据收发
 * 实现 IConnectionChannel 接口，提供统一的连接通道抽象
 */
public class BluetoothChannel implements IConnectionChannel {

    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "BluetoothChannel";
    private static final int DEFAULT_TIMEOUT = 5000;

    private BluetoothSocket mSocket;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private final Context mContext;
    private ConnectionConfig mConfig;
    private ConnectionState mState = ConnectionState.DISCONNECTED;
    private int mConnectionTimeout = DEFAULT_TIMEOUT;
    private int mReadTimeout = DEFAULT_TIMEOUT;
    private final List<ConnectionStateListener> mListeners = new ArrayList<>();

    public BluetoothChannel(Context context) {
        this.mContext = context;
    }

    // 兼容旧版构造函数
    public BluetoothChannel(Context context, String deviceMac) {
        this.mContext = context;
        this.mConfig = new ConnectionConfig(deviceMac, ConnectionConfig.ConnectionType.BLUETOOTH);
    }

    @Override
    public void connect(ConnectionConfig config) throws ConnectionException {
        if (config == null || config.getDeviceAddress() == null) {
            throw new ConnectionException("Invalid connection config", "INVALID_CONFIG", ConnectionType.BLUETOOTH);
        }

        this.mConfig = config;
        ConnectionState oldState = mState;
        setState(ConnectionState.CONNECTING);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            setState(ConnectionState.ERROR);
            String error = "Bluetooth connect permission not granted";
            notifyConnectionError(error);
            throw new ConnectionException(error, "PERMISSION_DENIED", ConnectionType.BLUETOOTH);
        }

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        }

        try {
            BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(config.getDeviceAddress());
            mSocket = device.createRfcommSocketToServiceRecord(SPP_UUID);
            mSocket.connect();
            mOutputStream = mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();
            setState(ConnectionState.CONNECTED);
            notifyConnected();
            LogManager.i(TAG, "Bluetooth connection successful, MAC: " + config.getDeviceAddress());
        } catch (IOException e) {
            setState(ConnectionState.ERROR);
            close();
            String error = "Bluetooth connection failed: " + e.getMessage();
            notifyConnectionError(error);
            throw new ConnectionException(error, e);
        }
    }

    // 兼容旧版 connect() 方法
    public boolean connect() {
        if (mConfig == null) {
            LogManager.e(TAG, "Connection config is null");
            return false;
        }
        try {
            connect(mConfig);
            return true;
        } catch (ConnectionException e) {
            LogManager.e(TAG, "Connect failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void disconnect() {
        ConnectionState oldState = mState;
        setState(ConnectionState.DISCONNECTING);
        close();
        setState(ConnectionState.DISCONNECTED);
        notifyDisconnected("User requested disconnect");
    }

    @Override
    public boolean isConnected() {
        return mSocket != null && mSocket.isConnected() && mState == ConnectionState.CONNECTED;
    }

    @Override
    public ConnectionState getConnectionState() {
        return mState;
    }

    @Override
    public void sendData(byte[] data) throws IOException {
        if (mOutputStream == null || data == null) {
            throw new IOException("Send failed: output stream is null or data is invalid");
        }
        try {
            mOutputStream.write(data);
            mOutputStream.flush();
            LogManager.d(TAG, "Sent data: " + bytesToHex(data));
        } catch (IOException e) {
            LogManager.e(TAG, "Send data failed: " + e.getMessage());
            setState(ConnectionState.ERROR);
            notifyConnectionError("Send data failed: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public byte[] receiveData() throws IOException {
        return receiveResponseBytes(mReadTimeout);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (mInputStream == null) {
            throw new IOException("Input stream is null");
        }
        return mInputStream;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (mOutputStream == null) {
            throw new IOException("Output stream is null");
        }
        return mOutputStream;
    }

    @Override
    public ConnectionConfig getConnectionConfig() {
        return mConfig;
    }

    @Override
    public void setConnectionTimeout(int timeoutMs) {
        this.mConnectionTimeout = timeoutMs;
        if (mConfig != null) {
            mConfig.setConnectionTimeout(timeoutMs);
        }
    }

    @Override
    public void setReadTimeout(int timeoutMs) {
        this.mReadTimeout = timeoutMs;
        if (mConfig != null) {
            mConfig.setReadTimeout(timeoutMs);
        }
    }

    @Override
    public void addConnectionStateListener(ConnectionStateListener listener) {
        if (listener != null && !mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    @Override
    public void removeConnectionStateListener(ConnectionStateListener listener) {
        mListeners.remove(listener);
    }

    private void setState(ConnectionState newState) {
        ConnectionState oldState = mState;
        if (oldState != newState) {
            mState = newState;
            notifyConnectionStateChanged(oldState, newState, null);
        }
    }

    private void notifyConnectionStateChanged(ConnectionState oldState, ConnectionState newState, String error) {
        for (ConnectionStateListener listener : new ArrayList<>(mListeners)) {
            try {
                listener.onConnectionStateChanged(oldState, newState, error);
            } catch (Exception e) {
                LogManager.e(TAG, "Error notifying listener: " + e.getMessage());
            }
        }
    }

    private void notifyConnected() {
        for (ConnectionStateListener listener : new ArrayList<>(mListeners)) {
            try {
                listener.onConnected();
            } catch (Exception e) {
                LogManager.e(TAG, "Error notifying listener: " + e.getMessage());
            }
        }
    }

    private void notifyDisconnected(String reason) {
        for (ConnectionStateListener listener : new ArrayList<>(mListeners)) {
            try {
                listener.onDisconnected(reason);
            } catch (Exception e) {
                LogManager.e(TAG, "Error notifying listener: " + e.getMessage());
            }
        }
    }

    private void notifyConnectionError(String error) {
        for (ConnectionStateListener listener : new ArrayList<>(mListeners)) {
            try {
                listener.onConnectionError(error);
            } catch (Exception e) {
                LogManager.e(TAG, "Error notifying listener: " + e.getMessage());
            }
        }
    }

    // ==================== 兼容旧版方法 ====================

    public boolean sendCommand(String command) {
        if (mOutputStream == null || command == null) {
            LogManager.w(TAG, "Send failed: output stream is null or command is invalid");
            return false;
        }
        try {
            sendData(command.getBytes("US-ASCII"));
            return true;
        } catch (IOException e) {
            LogManager.e(TAG, "Send command failed: " + e.getMessage());
            return false;
        }
    }

    public void sendCommand(byte[] command) throws IOException {
        sendData(command);
    }

    public byte[] receiveResponse() throws IOException {
        return receiveData();
    }

    public String receiveResponse(long timeout) {
        if (mInputStream == null) {
            LogManager.w(TAG, "Receive failed: input stream is null");
            return null;
        }
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            StringBuilder response = new StringBuilder();
            long startTime = System.currentTimeMillis();
            byte CR = 13;
            byte LF = 10;

            while (System.currentTimeMillis() - startTime < timeout) {
                if (mInputStream.available() > 0) {
                    bytesRead = mInputStream.read(buffer);
                    response.append(new String(buffer, 0, bytesRead, "US-ASCII"));
                    String resp = response.toString();
                    if (resp.length() >= 2 && resp.charAt(resp.length() - 2) == CR && resp.charAt(resp.length() - 1) == LF) {
                        String result = resp.trim();
                        LogManager.d(TAG, "Received response: " + result);
                        return result;
                    }
                }
                Thread.sleep(100);
            }
            LogManager.w(TAG, "Receive timeout (" + timeout + "ms)");
            return null;
        } catch (IOException | InterruptedException e) {
            LogManager.e(TAG, "Receive response failed: " + e.getMessage());
            Thread.currentThread().interrupt();
            return null;
        }
    }

    private byte[] receiveResponseBytes(long timeout) throws IOException {
        if (mInputStream == null) {
            throw new IOException("Receive failed: input stream is null");
        }
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            int totalBytes = 0;
            long startTime = System.currentTimeMillis();
            byte CR = 13;
            byte LF = 10;

            while (System.currentTimeMillis() - startTime < timeout) {
                if (mInputStream.available() > 0) {
                    bytesRead = mInputStream.read(buffer, totalBytes, buffer.length - totalBytes);
                    totalBytes += bytesRead;

                    if (totalBytes >= 2 && buffer[totalBytes - 2] == CR && buffer[totalBytes - 1] == LF) {
                        byte[] response = new byte[totalBytes];
                        System.arraycopy(buffer, 0, response, 0, totalBytes);
                        LogManager.d(TAG, "Received response: " + bytesToHex(response));
                        return response;
                    }

                    if (totalBytes >= buffer.length) {
                        byte[] newBuffer = new byte[buffer.length * 2];
                        System.arraycopy(buffer, 0, newBuffer, 0, totalBytes);
                        buffer = newBuffer;
                    }
                }
                Thread.sleep(100);
            }

            LogManager.w(TAG, "Receive timeout (" + timeout + "ms)");
            throw new IOException("Receive timeout after " + timeout + "ms");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Receive interrupted: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (mInputStream != null) mInputStream.close();
            if (mOutputStream != null) mOutputStream.close();
            if (mSocket != null) mSocket.close();
            LogManager.i(TAG, "Bluetooth connection closed");
        } catch (IOException e) {
            LogManager.e(TAG, "Close connection failed: " + e.getMessage());
        }
    }

    private String bytesToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}
