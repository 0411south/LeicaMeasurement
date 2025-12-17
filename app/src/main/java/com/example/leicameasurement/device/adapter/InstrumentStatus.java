// InstrumentStatus.java  
package com.example.leicameasurement.device.adapter;

/**
 * 仪器状态
 */
public class InstrumentStatus {
    private final boolean isBatteryOk;     // 电池状态
    private final boolean isLocked;        // 是否锁定
    private final boolean isErrorState;    // 是否错误状态
    private final String errorMessage;     // 错误信息

    public InstrumentStatus(boolean isBatteryOk, boolean isLocked,
                            boolean isErrorState, String errorMessage) {
        this.isBatteryOk = isBatteryOk;
        this.isLocked = isLocked;
        this.isErrorState = isErrorState;
        this.errorMessage = errorMessage;
    }

    // Getter 方法...
    public boolean isBatteryOk() { return isBatteryOk; }
    public boolean isLocked() { return isLocked; }
    public boolean isErrorState() { return isErrorState; }
    public String getErrorMessage() { return errorMessage; }
}