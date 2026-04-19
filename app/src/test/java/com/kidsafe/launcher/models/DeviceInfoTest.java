package com.kidsafe.launcher.models;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit tests for DeviceInfo model.
 */
public class DeviceInfoTest {

    @Test
    public void testBuilderBasic() {
        DeviceInfo info = new DeviceInfo.Builder()
                .setDeviceName("TestDevice")
                .setManufacturer("TestMfg")
                .setModel("TestModel")
                .build();

        assertEquals("TestDevice", info.getDeviceName());
        assertEquals("TestMfg", info.getManufacturer());
        assertEquals("TestModel", info.getModel());
    }

    @Test
    public void testBuilderAllFields() {
        DeviceInfo info = new DeviceInfo.Builder()
                .setDeviceName("Pixel")
                .setManufacturer("Google")
                .setModel("Pixel 8")
                .setAndroidVersion("14")
                .setSdkVersion(34)
                .setCpuArchitecture("arm64-v8a")
                .setTotalRam(8L * 1024 * 1024 * 1024)
                .setAvailableRam(4L * 1024 * 1024 * 1024)
                .setTotalStorage(128L * 1024 * 1024 * 1024)
                .setAvailableStorage(64L * 1024 * 1024 * 1024)
                .setScreenWidth(1080)
                .setScreenHeight(2400)
                .setScreenDensity(2.75f)
                .setBatteryLevel(85)
                .setSerialNumber("ABC123")
                .build();

        assertEquals("Pixel", info.getDeviceName());
        assertEquals("Google", info.getManufacturer());
        assertEquals("Pixel 8", info.getModel());
        assertEquals("14", info.getAndroidVersion());
        assertEquals(34, info.getSdkVersion());
        assertEquals("arm64-v8a", info.getCpuArchitecture());
        assertEquals(8L * 1024 * 1024 * 1024, info.getTotalRam());
        assertEquals(4L * 1024 * 1024 * 1024, info.getAvailableRam());
        assertEquals(128L * 1024 * 1024 * 1024, info.getTotalStorage());
        assertEquals(64L * 1024 * 1024 * 1024, info.getAvailableStorage());
        assertEquals(1080, info.getScreenWidth());
        assertEquals(2400, info.getScreenHeight());
        assertEquals(2.75f, info.getScreenDensity(), 0.01f);
        assertEquals(85, info.getBatteryLevel());
        assertEquals("ABC123", info.getSerialNumber());
    }

    @Test
    public void testDefaultValues() {
        DeviceInfo info = new DeviceInfo.Builder().build();

        assertEquals("", info.getDeviceName());
        assertEquals("", info.getManufacturer());
        assertEquals("", info.getModel());
        assertEquals("", info.getAndroidVersion());
        assertEquals(0, info.getSdkVersion());
        assertEquals("", info.getCpuArchitecture());
        assertEquals(0, info.getTotalRam());
        assertEquals(0, info.getAvailableRam());
        assertEquals(0, info.getTotalStorage());
        assertEquals(0, info.getAvailableStorage());
        assertEquals(0, info.getScreenWidth());
        assertEquals(0, info.getScreenHeight());
        assertEquals(0.0f, info.getScreenDensity(), 0.01f);
        assertEquals(0, info.getBatteryLevel());
        assertEquals("", info.getSerialNumber());
    }

    @Test
    public void testFormattedRam() {
        DeviceInfo info = new DeviceInfo.Builder()
                .setTotalRam(8L * 1024 * 1024 * 1024)
                .setAvailableRam(4L * 1024 * 1024 * 1024)
                .build();

        String formatted = info.getFormattedRam();
        assertTrue(formatted.contains("4.0 GB"));
        assertTrue(formatted.contains("8.0 GB"));
        assertTrue(formatted.contains("/"));
    }

    @Test
    public void testFormattedStorage() {
        DeviceInfo info = new DeviceInfo.Builder()
                .setTotalStorage(128L * 1024 * 1024 * 1024)
                .setAvailableStorage(64L * 1024 * 1024 * 1024)
                .build();

        String formatted = info.getFormattedStorage();
        assertTrue(formatted.contains("64.0 GB"));
        assertTrue(formatted.contains("128.0 GB"));
    }

    @Test
    public void testScreenResolution() {
        DeviceInfo info = new DeviceInfo.Builder()
                .setScreenWidth(1080)
                .setScreenHeight(2400)
                .build();

        assertEquals("1080 x 2400", info.getScreenResolution());
    }

    @Test
    public void testFormatBytesBytes() {
        assertEquals("500 B", DeviceInfo.formatBytes(500));
        assertEquals("0 B", DeviceInfo.formatBytes(0));
        assertEquals("1023 B", DeviceInfo.formatBytes(1023));
    }

    @Test
    public void testFormatBytesKB() {
        assertEquals("1.0 KB", DeviceInfo.formatBytes(1024));
        assertEquals("1.5 KB", DeviceInfo.formatBytes(1536));
        assertEquals("1023.0 KB", DeviceInfo.formatBytes(1023 * 1024));
    }

    @Test
    public void testFormatBytesMB() {
        assertEquals("1.0 MB", DeviceInfo.formatBytes(1024 * 1024));
        assertEquals("512.0 MB", DeviceInfo.formatBytes(512L * 1024 * 1024));
    }

    @Test
    public void testFormatBytesGB() {
        assertEquals("1.0 GB", DeviceInfo.formatBytes(1024L * 1024 * 1024));
        assertEquals("8.0 GB", DeviceInfo.formatBytes(8L * 1024 * 1024 * 1024));
    }

    @Test
    public void testScreenResolutionZero() {
        DeviceInfo info = new DeviceInfo.Builder().build();
        assertEquals("0 x 0", info.getScreenResolution());
    }

    @Test
    public void testFormattedRamZero() {
        DeviceInfo info = new DeviceInfo.Builder().build();
        assertEquals("0 B / 0 B", info.getFormattedRam());
    }

    @Test
    public void testFormattedStorageZero() {
        DeviceInfo info = new DeviceInfo.Builder().build();
        assertEquals("0 B / 0 B", info.getFormattedStorage());
    }

    @Test
    public void testBuilderChaining() {
        DeviceInfo.Builder builder = new DeviceInfo.Builder();
        DeviceInfo.Builder result = builder.setDeviceName("test");
        assertSame(builder, result);

        result = builder.setManufacturer("mfg");
        assertSame(builder, result);

        result = builder.setModel("model");
        assertSame(builder, result);

        result = builder.setAndroidVersion("14");
        assertSame(builder, result);

        result = builder.setSdkVersion(34);
        assertSame(builder, result);

        result = builder.setCpuArchitecture("arm64");
        assertSame(builder, result);

        result = builder.setTotalRam(100);
        assertSame(builder, result);

        result = builder.setAvailableRam(50);
        assertSame(builder, result);

        result = builder.setTotalStorage(200);
        assertSame(builder, result);

        result = builder.setAvailableStorage(100);
        assertSame(builder, result);

        result = builder.setScreenWidth(1080);
        assertSame(builder, result);

        result = builder.setScreenHeight(1920);
        assertSame(builder, result);

        result = builder.setScreenDensity(3.0f);
        assertSame(builder, result);

        result = builder.setBatteryLevel(50);
        assertSame(builder, result);

        result = builder.setSerialNumber("123");
        assertSame(builder, result);
    }
}
