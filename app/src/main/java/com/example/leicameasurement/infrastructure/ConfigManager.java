package com.example.leicameasurement.infrastructure;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 配置管理（存储仪器型号/蓝牙MAC）
 */
public class ConfigManager {

    private static final String PREF_NAME = "LeicaMeasurementConfig";
    private static final String KEY_INSTRUMENT_MODEL = "instrument_model";
    private static final String KEY_BLUETOOTH_MAC = "bluetooth_mac";

    private final SharedPreferences sharedPreferences;

    public ConfigManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveInstrumentModel(String model) {
        sharedPreferences.edit().putString(KEY_INSTRUMENT_MODEL, model).apply();
    }

    public String getInstrumentModel() {
        return sharedPreferences.getString(KEY_INSTRUMENT_MODEL, "TS60"); // Default to TS60
    }

    public void saveBluetoothMac(String macAddress) {
        sharedPreferences.edit().putString(KEY_BLUETOOTH_MAC, macAddress).apply();
    }

    public String getBluetoothMac() {
        return sharedPreferences.getString(KEY_BLUETOOTH_MAC, null);
    }
}
