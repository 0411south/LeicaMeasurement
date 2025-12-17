package com.example.leicameasurement.controller;

import com.example.leicameasurement.device.InstrumentType;
import com.example.leicameasurement.device.adapter.InstrumentAdapter;
import com.example.leicameasurement.device.adapter.InstrumentFactory;
import com.example.leicameasurement.device.bluetooth.BluetoothLinkManager;

/**
 * 测量总入口（分发指令）
 */
public class MeasurementController {

    private final BluetoothLinkManager linkManager;
    private final InstrumentAdapter instrument;

    public MeasurementController(BluetoothLinkManager linkManager, String instrumentModel) {
        this.linkManager = linkManager;
        InstrumentType type = InstrumentType.fromString(instrumentModel);
        this.instrument = InstrumentFactory.createInstrument(type, linkManager);
    }

    public void connect() {
        linkManager.connect();
    }

    public void disconnect() {
        linkManager.disconnect();
    }

    public BluetoothLinkManager.LinkState getLinkState() {
        return linkManager.getState();
    }

    public InstrumentAdapter getInstrument() {
        return instrument;
    }
}
