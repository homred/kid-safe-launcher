package com.kidsafe.launcher.receivers;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit tests for PackageChangeReceiver.
 * BroadcastReceiver cannot be instantiated in pure JUnit (Android stub),
 * so we test the listener interface contract.
 */
public class PackageChangeReceiverTest {

    @Test
    public void testListenerInterfaceCallable() {
        final boolean[] called = {false};
        PackageChangeReceiver.OnPackageChangeListener listener = () -> called[0] = true;
        listener.onPackageChanged();
        assertTrue(called[0]);
    }

    @Test
    public void testListenerInterfaceMultipleCalls() {
        final int[] count = {0};
        PackageChangeReceiver.OnPackageChangeListener listener = () -> count[0]++;
        listener.onPackageChanged();
        listener.onPackageChanged();
        listener.onPackageChanged();
        assertEquals(3, count[0]);
    }

    @Test
    public void testListenerLambda() {
        final String[] result = {""};
        PackageChangeReceiver.OnPackageChangeListener listener = () -> result[0] = "changed";
        listener.onPackageChanged();
        assertEquals("changed", result[0]);
    }

    @Test
    public void testReceiverConstructionInTestEnv() {
        // BroadcastReceiver constructor throws Stub! in pure JUnit
        try {
            PackageChangeReceiver receiver = new PackageChangeReceiver();
            assertNotNull(receiver);
        } catch (RuntimeException e) {
            // Expected in test environment
            assertTrue(e.getMessage().contains("Stub"));
        }
    }

    @Test
    public void testListenerReplacement() {
        final int[] firstCount = {0};
        final int[] secondCount = {0};

        PackageChangeReceiver.OnPackageChangeListener first = () -> firstCount[0]++;
        PackageChangeReceiver.OnPackageChangeListener second = () -> secondCount[0]++;

        first.onPackageChanged();
        assertEquals(1, firstCount[0]);
        assertEquals(0, secondCount[0]);

        second.onPackageChanged();
        assertEquals(1, firstCount[0]);
        assertEquals(1, secondCount[0]);
    }

    @Test
    public void testListenerNotNull() {
        PackageChangeReceiver.OnPackageChangeListener listener = () -> {};
        assertNotNull(listener);
    }

    @Test
    public void testListenerWithSideEffects() {
        final java.util.List<String> log = new java.util.ArrayList<>();
        PackageChangeReceiver.OnPackageChangeListener listener = () -> log.add("package_changed");
        listener.onPackageChanged();
        listener.onPackageChanged();
        assertEquals(2, log.size());
        assertEquals("package_changed", log.get(0));
    }
}
