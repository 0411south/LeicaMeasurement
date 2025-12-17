package com.example.leicameasurement.device.adapter;

import android.content.Context;

import com.example.leicameasurement.device.InstrumentType;
import com.example.leicameasurement.device.bluetooth.BluetoothChannel;
import com.example.leicameasurement.device.bluetooth.BluetoothLinkManager;
import com.example.leicameasurement.device.connection.IConnectionChannel;

public class InstrumentFactory {

    /**
     * 根据仪器类型创建并初始化对应的适配器实例。
     *
     * @param type        仪器类型 (TS30, TS60, etc.)
     * @param linkManager 用于创建连接通道的蓝牙链接管理器
     * @return 返回一个已经初始化好的仪器适配器
     * @throws IllegalArgumentException 如果仪器类型不支持
     */
    public static InstrumentAdapter createInstrument(InstrumentType type, BluetoothLinkManager linkManager) {

        InstrumentAdapter adapter;

        switch (type) {
            case TS30:
                // TS30Adapter 需要 BluetoothLinkManager 参数
                adapter = new TS30Adapter(linkManager);
                break;
            case TS60:
                // TS60Adapter 使用 initialize 模式，先创建再初始化
                adapter = new TS60Adapter();
                // 注意：TS60Adapter 需要通过 initialize 方法注入连接通道
                // 这里我们需要创建一个 IConnectionChannel 实例
                // 但 BluetoothChannel 需要 Context 和 deviceAddress
                // 这个设计有问题，我们暂时先让 TS60Adapter 也使用构造函数注入
                // 或者，我们需要从 linkManager 中获取必要的信息

                // 临时方案：假设 linkManager 有 getContext() 和 getDeviceAddress() 方法
                // 如果没有，你需要修改 BluetoothLinkManager 类添加这些方法
                Context context = linkManager.getContext();
                String deviceAddress = linkManager.getDeviceAddress();
                IConnectionChannel connectionChannel = new BluetoothChannel(context, deviceAddress);
                adapter.initialize(connectionChannel);
                break;
            default:
                throw new IllegalArgumentException("不支持的仪器类型: " + type);
        }

        return adapter;
    }
}
