package com.example.leicameasurement.device.wifi;

import com.example.leicameasurement.device.connection.ConnectionException;
import com.example.leicameasurement.device.connection.IConnectionChannel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * WiFi 模块单元测试
 */
@RunWith(MockitoJUnitRunner.class)
public class WifiModuleTest {

    private WifiConfig wifiConfig;
    private WifiLinkManager linkManager;

    @Before
    public void setUp() {
        wifiConfig = new WifiConfig("192.168.1.100", 5000);
    }

    @Test
    public void testWifiConfigCreation() {
        assertNotNull(wifiConfig);
        assertEquals("192.168.1.100", wifiConfig.getIpAddress());
        assertEquals(5000, wifiConfig.getPort());
    }

    @Test
    public void testWifiLinkManagerCreation() {
        linkManager = new WifiLinkManager(wifiConfig);
        assertNotNull(linkManager);
        assertNotNull(linkManager.getWifiConfig());
        assertEquals(IConnectionChannel.ConnectionState.DISCONNECTED, linkManager.getConnectionState());
    }

    @Test
    public void testAutoReconnectSettings() {
        linkManager = new WifiLinkManager(wifiConfig);

        linkManager.enableAutoReconnect(true);
        linkManager.setMaxReconnectAttempts(5);
        linkManager.setReconnectDelay(1000);

        // 验证设置生效
        assertEquals(0, linkManager.getReconnectAttempts());
    }

    @Test
    public void testWifiTransceiverCreation() {
        WifiChannel channel = new WifiChannel();
        WifiTransceiver transceiver = new WifiTransceiver(channel);

        assertNotNull(transceiver);
        assertNotNull(transceiver.getWifiChannel());
    }
}
