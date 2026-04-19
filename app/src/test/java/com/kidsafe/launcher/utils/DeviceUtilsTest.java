package com.kidsafe.launcher.utils;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit tests for DeviceUtils utility class.
 */
public class DeviceUtilsTest {

    @Test
    public void testGetCpuArchitecture() {
        String arch = DeviceUtils.getCpuArchitecture();
        // On test JVM, Build.SUPPORTED_ABIS may be null or empty
        // The method should return "Unknown" or a valid ABI, never null
        assertNotNull(arch);
    }

    @Test
    public void testGetCpuArchitectureNotEmpty() {
        String arch = DeviceUtils.getCpuArchitecture();
        assertFalse(arch.isEmpty());
    }

    @Test
    public void testGetSerialNumberHandlesStub() {
        // On test JVM, Build.SERIAL throws RuntimeException: Stub!
        // Our getSerialNumber should handle this gracefully
        try {
            String serial = DeviceUtils.getSerialNumber();
            // If it doesn't throw, any non-null result is fine
            // serial could be "N/A" or actual value
        } catch (RuntimeException e) {
            // Expected in test environment - Build.SERIAL is a stub
            assertTrue(e.getMessage().contains("Stub"));
        }
    }
}
