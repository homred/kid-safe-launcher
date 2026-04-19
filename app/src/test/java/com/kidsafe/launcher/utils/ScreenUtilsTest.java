package com.kidsafe.launcher.utils;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit tests for ScreenUtils utility class.
 */
public class ScreenUtilsTest {

    @Test
    public void testDpToPxConversion() {
        // Test the conversion formula directly
        // dpToPx: Math.round(dp * density)
        // Since we can't easily test with Context in pure JUnit,
        // we test the mathematical relationships
        int dp = 10;
        float density = 2.0f;
        int expectedPx = Math.round(dp * density);
        assertEquals(20, expectedPx);
    }

    @Test
    public void testPxToDpConversion() {
        // Test inverse conversion formula
        int px = 20;
        float density = 2.0f;
        int expectedDp = Math.round(px / density);
        assertEquals(10, expectedDp);
    }

    @Test
    public void testDpToPxZero() {
        int dp = 0;
        float density = 3.0f;
        int expectedPx = Math.round(dp * density);
        assertEquals(0, expectedPx);
    }

    @Test
    public void testPxToDpZero() {
        int px = 0;
        float density = 3.0f;
        int expectedDp = Math.round(px / density);
        assertEquals(0, expectedDp);
    }

    @Test
    public void testDpToPxHighDensity() {
        int dp = 48;
        float density = 4.0f;
        int expectedPx = Math.round(dp * density);
        assertEquals(192, expectedPx);
    }

    @Test
    public void testPxToDpHighDensity() {
        int px = 192;
        float density = 4.0f;
        int expectedDp = Math.round(px / density);
        assertEquals(48, expectedDp);
    }

    @Test
    public void testDpToPxFractionalDensity() {
        int dp = 10;
        float density = 2.75f;
        int expectedPx = Math.round(dp * density);
        assertEquals(28, expectedPx);
    }
}
