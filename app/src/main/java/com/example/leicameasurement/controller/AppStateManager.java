package com.example.leicameasurement.controller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.leicameasurement.device.bluetooth.BluetoothLinkManager;
import com.example.leicameasurement.device.connection.ConnectionType;
import com.example.leicameasurement.device.connection.ConnectionState;

/**
 * 全局状态管理（当前任务/测站/连接状态）
 */
public class AppStateManager {

    private static volatile AppStateManager instance;

    // 连接状态
    private final MutableLiveData<BluetoothLinkManager.LinkState> linkState = new MutableLiveData<>(BluetoothLinkManager.LinkState.DISCONNECTED);
    private final MutableLiveData<ConnectionType> currentConnectionType = new MutableLiveData<>(ConnectionType.BLUETOOTH);
    private final MutableLiveData<ConnectionState> connectionState = new MutableLiveData<>(ConnectionState.DISCONNECTED);

    // 任务和测站ID
    private final MutableLiveData<Long> currentTaskId = new MutableLiveData<>();
    private final MutableLiveData<Long> currentStationId = new MutableLiveData<>();

    // 测站坐标信息
    private final MutableLiveData<Double> currentStationX = new MutableLiveData<>(5000.000);
    private final MutableLiveData<Double> currentStationY = new MutableLiveData<>(3000.000);
    private final MutableLiveData<Double> currentStationZ = new MutableLiveData<>(100.000);
    private final MutableLiveData<Double> currentInstrumentHeight = new MutableLiveData<>(1.500);

    // 默认棱镜高
    private final MutableLiveData<Double> currentPrismHeight = new MutableLiveData<>(1.600);

    private AppStateManager() {
        // 私有构造函数，单例模式
    }

    public static AppStateManager getInstance() {
        if (instance == null) {
            synchronized (AppStateManager.class) {
                if (instance == null) {
                    instance = new AppStateManager();
                }
            }
        }
        return instance;
    }

    // ==================== 连接状态相关方法 ====================

    public LiveData<BluetoothLinkManager.LinkState> getLinkState() {
        return linkState;
    }

    public void setLinkState(BluetoothLinkManager.LinkState state) {
        if (linkState.getValue() != state) {
            linkState.postValue(state);
        }
    }

    public LiveData<ConnectionType> getCurrentConnectionType() {
        return currentConnectionType;
    }

    public void setCurrentConnectionType(ConnectionType connectionType) {
        if (currentConnectionType.getValue() != connectionType) {
            currentConnectionType.postValue(connectionType);
        }
    }

    public LiveData<ConnectionState> getConnectionState() {
        return connectionState;
    }

    public void setConnectionState(ConnectionState state) {
        if (connectionState.getValue() != state) {
            connectionState.postValue(state);
        }
    }

    // ==================== 任务和测站ID相关方法 ====================

    public LiveData<Long> getCurrentTaskId() {
        return currentTaskId;
    }

    public void setCurrentTaskId(long taskId) {
        currentTaskId.postValue(taskId);
    }

    public LiveData<Long> getCurrentStationId() {
        return currentStationId;
    }

    public void setCurrentStationId(long stationId) {
        currentStationId.postValue(stationId);
    }

    // ==================== 测站坐标相关方法 ====================

    public LiveData<Double> getCurrentStationX() {
        return currentStationX;
    }

    public double getCurrentStationXValue() {
        Double value = currentStationX.getValue();
        return value != null ? value : 5000.000;
    }

    public void setCurrentStationX(double x) {
        currentStationX.postValue(x);
    }

    public LiveData<Double> getCurrentStationY() {
        return currentStationY;
    }

    public double getCurrentStationYValue() {
        Double value = currentStationY.getValue();
        return value != null ? value : 3000.000;
    }

    public void setCurrentStationY(double y) {
        currentStationY.postValue(y);
    }

    public LiveData<Double> getCurrentStationZ() {
        return currentStationZ;
    }

    public double getCurrentStationZValue() {
        Double value = currentStationZ.getValue();
        return value != null ? value : 100.000;
    }

    public void setCurrentStationZ(double z) {
        currentStationZ.postValue(z);
    }

    public LiveData<Double> getCurrentInstrumentHeight() {
        return currentInstrumentHeight;
    }

    public double getCurrentInstrumentHeightValue() {
        Double value = currentInstrumentHeight.getValue();
        return value != null ? value : 1.500;
    }

    public void setCurrentInstrumentHeight(double height) {
        currentInstrumentHeight.postValue(height);
    }

    public LiveData<Double> getCurrentPrismHeight() {
        return currentPrismHeight;
    }

    public double getCurrentPrismHeightValue() {
        Double value = currentPrismHeight.getValue();
        return value != null ? value : 1.600;
    }

    public void setCurrentPrismHeight(double height) {
        currentPrismHeight.postValue(height);
    }

    // ==================== 批量设置方法 ====================

    /**
     * 设置测站信息
     * @param x X坐标
     * @param y Y坐标
     * @param z Z坐标
     * @param instrumentHeight 仪器高
     */
    public void setStationInfo(double x, double y, double z, double instrumentHeight) {
        setCurrentStationX(x);
        setCurrentStationY(y);
        setCurrentStationZ(z);
        setCurrentInstrumentHeight(instrumentHeight);
    }

    /**
     * 设置测站信息（使用默认仪器高）
     * @param x X坐标
     * @param y Y坐标
     * @param z Z坐标
     */
    public void setStationInfo(double x, double y, double z) {
        setStationInfo(x, y, z, getCurrentInstrumentHeightValue());
    }

    /**
     * 设置连接信息
     * @param connectionType 连接类型
     * @param connectionState 连接状态
     */
    public void setConnectionInfo(ConnectionType connectionType, ConnectionState connectionState) {
        setCurrentConnectionType(connectionType);
        setConnectionState(connectionState);
    }

    // ==================== 工具方法 ====================

    /**
     * 检查是否已连接
     * @return true=已连接
     */
    public boolean isConnected() {
        ConnectionState state = connectionState.getValue();
        return state != null && state == ConnectionState.CONNECTED;
    }

    /**
     * 检查当前连接类型
     * @return 连接类型
     */
    public ConnectionType getConnectionType() {
        ConnectionType type = currentConnectionType.getValue();
        return type != null ? type : ConnectionType.BLUETOOTH;
    }

    /**
     * 获取当前任务ID字符串（用于数据库存储）
     * @return 任务ID字符串
     */
    public String getCurrentTaskIdString() {
        Long taskId = currentTaskId.getValue();
        return taskId != null ? "TASK_" + taskId : "TASK_DEFAULT";
    }

    /**
     * 重置为默认测站坐标
     */
    public void resetToDefaultStation() {
        setStationInfo(5000.000, 3000.000, 100.000, 1.500);
        setCurrentPrismHeight(1.600);
    }

    /**
     * 获取测站信息字符串
     * @return 格式化的测站信息
     */
    public String getStationInfoString() {
        return String.format("E: %.3f m, N: %.3f m, H: %.3f m",
                getCurrentStationXValue(), getCurrentStationYValue(), getCurrentStationZValue());
    }

    /**
     * 获取仪器信息字符串
     * @return 格式化的仪器信息
     */
    public String getInstrumentInfoString() {
        return String.format("仪器高: %.3f m, 棱镜高: %.3f m",
                getCurrentInstrumentHeightValue(), getCurrentPrismHeightValue());
    }
}