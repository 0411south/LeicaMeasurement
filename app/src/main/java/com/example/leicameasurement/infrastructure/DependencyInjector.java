package com.example.leicameasurement.infrastructure;

import android.content.Context;
import com.example.leicameasurement.calculation.TraverseCalculator;
import com.example.leicameasurement.calculation.DetailPointCalculator;
import com.example.leicameasurement.controller.AppStateManager;
import com.example.leicameasurement.controller.DetailPointController;
import com.example.leicameasurement.controller.MeasurementController;
import com.example.leicameasurement.controller.TraverseController;
import com.example.leicameasurement.data.repository.DetailPointRepository;
import com.example.leicameasurement.data.repository.TraverseRepository;
import com.example.leicameasurement.data.wal.WalLogManager;
import com.example.leicameasurement.device.adapter.InstrumentAdapter;
import com.example.leicameasurement.device.adapter.InstrumentFactory;
import com.example.leicameasurement.device.bluetooth.BluetoothLinkManager;

/**
 * 依赖注入（伪）
 */
public class DependencyInjector {

    private static BluetoothLinkManager sBluetoothLinkManager;
    private static AppStateManager sAppStateManager;

    public static MeasurementController provideMeasurementController(Context context) {
        ConfigManager configManager = new ConfigManager(context.getApplicationContext());
        String macAddress = configManager.getBluetoothMac();
        String instrumentModel = configManager.getInstrumentModel();

        if (sBluetoothLinkManager == null && macAddress != null) {
            sBluetoothLinkManager = new BluetoothLinkManager(context.getApplicationContext(), macAddress);
        }

        return new MeasurementController(sBluetoothLinkManager, instrumentModel);
    }

    public static TraverseController provideTraverseController(Context context) {
        MeasurementController measurementController = provideMeasurementController(context);
        InstrumentAdapter instrument = measurementController.getInstrument();
        TraverseCalculator calculator = new TraverseCalculator();
        // These would be properly initialized in a real app with a database
        TraverseRepository repository = new TraverseRepository();
        WalLogManager walLogManager = new WalLogManager();
        return new TraverseController(instrument, calculator, repository, walLogManager);
    }

    public static DetailPointController provideDetailPointController(Context context) {
        // 修复：提供所有必需的依赖项
        MeasurementController measurementController = provideMeasurementController(context);
        InstrumentAdapter instrument = measurementController.getInstrument();
        DetailPointCalculator calculator = new DetailPointCalculator();
        DetailPointRepository repository = new DetailPointRepository();
        AppStateManager appStateManager = provideAppStateManager(context);

        return new DetailPointController(instrument, calculator, repository, appStateManager);
    }

    public static AppStateManager provideAppStateManager(Context context) {
        // 修复：使用单例模式而不是直接new
        if (sAppStateManager == null) {
            sAppStateManager = AppStateManager.getInstance();
        }
        return sAppStateManager;
    }

    public static InstrumentAdapter provideInstrumentAdapter(Context context) {
        MeasurementController measurementController = provideMeasurementController(context);
        return measurementController.getInstrument();
    }

    public static BluetoothLinkManager provideBluetoothLinkManager(Context context) {
        if (sBluetoothLinkManager == null) {
            ConfigManager configManager = new ConfigManager(context.getApplicationContext());
            String macAddress = configManager.getBluetoothMac();
            if (macAddress != null) {
                sBluetoothLinkManager = new BluetoothLinkManager(context.getApplicationContext(), macAddress);
            }
        }
        return sBluetoothLinkManager;
    }

    public static DetailPointCalculator provideDetailPointCalculator() {
        return new DetailPointCalculator();
    }

    public static TraverseCalculator provideTraverseCalculator() {
        return new TraverseCalculator();
    }

    public static DetailPointRepository provideDetailPointRepository() {
        return new DetailPointRepository();
    }

    public static TraverseRepository provideTraverseRepository() {
        return new TraverseRepository();
    }

    public static WalLogManager provideWalLogManager() {
        return new WalLogManager();
    }

    /**
     * 清理资源
     */
    public static void cleanup() {
        if (sBluetoothLinkManager != null) {
            sBluetoothLinkManager.disconnect();
            sBluetoothLinkManager = null;
        }
        sAppStateManager = null;
    }
}