package com.example.leicameasurement.device.adapter;

/**
 * 仪器信息
 */
public class InstrumentInfo {
    private final String model;        // 仪器型号
    private final String serialNumber; // 序列号
    private final String firmwareVersion; // 固件版本
    private final String manufacturer; // 制造商

    public InstrumentInfo(String model, String serialNumber, String firmwareVersion, String manufacturer) {
        this.model = model;
        this.serialNumber = serialNumber;
        this.firmwareVersion = firmwareVersion;
        this.manufacturer = manufacturer;
    }

    // Getter 方法...
    public String getModel() { return model; }
    public String getSerialNumber() { return serialNumber; }
    public String getFirmwareVersion() { return firmwareVersion; }
    public String getManufacturer() { return manufacturer; }
}