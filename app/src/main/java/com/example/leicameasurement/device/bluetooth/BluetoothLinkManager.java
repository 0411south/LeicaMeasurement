package com.example.leicameasurement.device.bluetooth;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.example.leicameasurement.infrastructure.LogManager;
import java.io.IOException;

/**
 * 蓝牙链路总控：封装状态机、对外提供统一接口
 * 核心职责：管理连接/断连/重连，提供同步/异步的指令收发接口
 */
public class BluetoothLinkManager {

    private static final String TAG = "BluetoothLinkManager";
    private static final long DEFAULT_TIMEOUT = 5000; // 默认超时

    private final String mDeviceMac;
    private final Context mContext;
    private BluetoothChannel mChannel;
    private DataTransceiver mTransceiver;
    private ConnectionMonitor mMonitor;
    private final Handler mMainHandler = new Handler(Looper.getMainLooper()); // 用于UI回调

    private volatile LinkState mState = LinkState.DISCONNECTED;
    private LinkStateListener mListener;

    public BluetoothLinkManager(Context context, String deviceMac) {
        this.mContext = context;
        this.mDeviceMac = deviceMac;
    }

    /**
     * 连接状态监听器
     */
    public interface LinkStateListener {
        void onStateChanged(LinkState state);
    }

    /**
     * 设置监听器
     */
    public void setListener(LinkStateListener listener) {
        this.mListener = listener;
    }

    /**
     * 启动连接
     */
    public synchronized void connect() {
        if (mState != LinkState.DISCONNECTED) return;
        updateState(LinkState.CONNECTING);
        new Thread(() -> {
            mChannel = new BluetoothChannel(mContext, mDeviceMac);
            if (mChannel.connect()) {
                mTransceiver = new DataTransceiver(mChannel);
                mTransceiver.start();
                mMonitor = new ConnectionMonitor(mChannel, this::handleDisconnect);
                mMonitor.start();
                updateState(LinkState.CONNECTED);
            } else {
                updateState(LinkState.DISCONNECTED);
            }
        }).start();
    }

    /**
     * 断开连接
     */
    public synchronized void disconnect() {
        if (mState == LinkState.DISCONNECTED) return;
        handleDisconnect();
    }

    /**
     * 处理断连（由心跳或手动触发）
     */
    private synchronized void handleDisconnect() {
        if (mState == LinkState.DISCONNECTED) return;
        updateState(LinkState.DISCONNECTED);
        if (mMonitor != null) mMonitor.stop();
        if (mTransceiver != null) mTransceiver.stop();
        if (mChannel != null) mChannel.close();
        LogManager.w(TAG, "蓝牙链路已断开");
    }

    /**
     * 发送指令并等待响应（同步）
     * @param command 指令
     * @param timeout 超时时间
     * @return 响应
     */
    public String sendAndReceive(String command, long timeout) {
        if (mState != LinkState.CONNECTED || mTransceiver == null) {
            LogManager.e(TAG, "发送失败：链路未连接");
            return null;
        }
        return mTransceiver.sendAndReceive(command, timeout);
    }

    /**
     * 异步发送指令（原有方法，接受 String 参数）
     * @param command 指令
     */
    public void sendCommand(String command) {
        if (mState != LinkState.CONNECTED || mTransceiver == null) {
            LogManager.e(TAG, "发送失败：链路未连接");
            return;
        }
        mTransceiver.sendCommand(command);
    }

    // ==================== 新增方法：支持 TS30Adapter 等适配器 ====================

    /**
     * 发送字节数组指令（新增方法，用于支持 byte[] 类型的指令）
     * @param command 字节数组指令
     * @throws IOException 如果发送失败
     */
    public void sendCommand(byte[] command) throws IOException {
        if (mState != LinkState.CONNECTED || mTransceiver == null) {
            throw new IOException("发送失败：链路未连接");
        }

        // 将 byte[] 转换为 String（根据你的协议，可能需要调整编码方式）
        // 如果你的协议是二进制的，可以直接传递给 DataTransceiver
        // 这里假设你的 DataTransceiver 内部可以处理 byte[]

        // 方案1：如果 DataTransceiver 只支持 String，转换为 String
        String commandStr = new String(command, "UTF-8"); // 或者使用其他编码
        mTransceiver.sendCommand(commandStr);

        // 方案2：如果你想直接发送 byte[]，需要修改 DataTransceiver 添加对应方法
        // mTransceiver.sendCommand(command);
    }

    /**
     * 接收响应数据（新增方法）
     * @return 响应的字节数组
     * @throws IOException 如果接收失败
     */
    public byte[] receiveResponse() throws IOException {
        if (mState != LinkState.CONNECTED || mTransceiver == null) {
            throw new IOException("接收失败：链路未连接");
        }

        // 使用默认超时时间接收响应
        String response = mTransceiver.sendAndReceive("", DEFAULT_TIMEOUT);

        if (response == null) {
            throw new IOException("接收响应超时");
        }

        // 将 String 转换为 byte[]
        return response.getBytes("UTF-8"); // 或者使用其他编码
    }

    /**
     * 判断是否已连接（新增方法）
     * @return true 如果已连接，否则 false
     */
    public boolean isConnected() {
        return mState == LinkState.CONNECTED && mChannel != null && mChannel.isConnected();
    }

    /**
     * 获取 Context（新增方法，用于 InstrumentFactory）
     * @return Context 对象
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * 获取设备地址（新增方法，用于 InstrumentFactory）
     * @return 设备 MAC 地址
     */
    public String getDeviceAddress() {
        return mDeviceMac;
    }

    // ==================== 原有方法 ====================

    /**
     * 更新状态并通知监听器
     */
    private void updateState(LinkState newState) {
        if (mState == newState) return;
        mState = newState;
        LogManager.i(TAG, "链路状态更新为：" + newState);
        if (mListener != null) {
            mMainHandler.post(() -> mListener.onStateChanged(newState));
        }
    }

    /**
     * 获取当前状态
     */
    public LinkState getState() {
        return mState;
    }

    /**
     * 链路状态枚举
     */
    public enum LinkState {
        DISCONNECTED, // 已断开
        CONNECTING,   // 连接中
        CONNECTED,    // 已连接
        RECONNECTING  // 重连中（暂未实现）
    }
}
