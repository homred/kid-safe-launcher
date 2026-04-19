package com.kidsafe.launcher.models;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit tests for ScreenSize enum.
 */
public class ScreenSizeTest {

    @Test
    public void testWatchRange() {
        assertEquals(0, ScreenSize.WATCH.getMinWidthDp());
        assertEquals(320, ScreenSize.WATCH.getMaxWidthDp());
    }

    @Test
    public void testPhoneRange() {
        assertEquals(320, ScreenSize.PHONE.getMinWidthDp());
        assertEquals(480, ScreenSize.PHONE.getMaxWidthDp());
    }

    @Test
    public void testPhabletRange() {
        assertEquals(480, ScreenSize.PHABLET.getMinWidthDp());
        assertEquals(600, ScreenSize.PHABLET.getMaxWidthDp());
    }

    @Test
    public void testTabletRange() {
        assertEquals(600, ScreenSize.TABLET.getMinWidthDp());
        assertEquals(720, ScreenSize.TABLET.getMaxWidthDp());
    }

    @Test
    public void testTvRange() {
        assertEquals(720, ScreenSize.TV.getMinWidthDp());
        assertEquals(Integer.MAX_VALUE, ScreenSize.TV.getMaxWidthDp());
    }

    @Test
    public void testShouldShowStatusBar() {
        assertFalse(ScreenSize.WATCH.shouldShowStatusBar());
        assertFalse(ScreenSize.PHONE.shouldShowStatusBar());
        assertTrue(ScreenSize.PHABLET.shouldShowStatusBar());
        assertTrue(ScreenSize.TABLET.shouldShowStatusBar());
        assertTrue(ScreenSize.TV.shouldShowStatusBar());
    }

    @Test
    public void testGridColumns() {
        assertEquals(2, ScreenSize.WATCH.getGridColumns());
        assertEquals(3, ScreenSize.PHONE.getGridColumns());
        assertEquals(4, ScreenSize.PHABLET.getGridColumns());
        assertEquals(5, ScreenSize.TABLET.getGridColumns());
        assertEquals(6, ScreenSize.TV.getGridColumns());
    }

    @Test
    public void testFromWidthDpWatch() {
        assertEquals(ScreenSize.WATCH, ScreenSize.fromWidthDp(0));
        assertEquals(ScreenSize.WATCH, ScreenSize.fromWidthDp(100));
        assertEquals(ScreenSize.WATCH, ScreenSize.fromWidthDp(200));
        assertEquals(ScreenSize.WATCH, ScreenSize.fromWidthDp(319));
    }

    @Test
    public void testFromWidthDpPhone() {
        assertEquals(ScreenSize.PHONE, ScreenSize.fromWidthDp(320));
        assertEquals(ScreenSize.PHONE, ScreenSize.fromWidthDp(400));
        assertEquals(ScreenSize.PHONE, ScreenSize.fromWidthDp(479));
    }

    @Test
    public void testFromWidthDpPhablet() {
        assertEquals(ScreenSize.PHABLET, ScreenSize.fromWidthDp(480));
        assertEquals(ScreenSize.PHABLET, ScreenSize.fromWidthDp(540));
        assertEquals(ScreenSize.PHABLET, ScreenSize.fromWidthDp(599));
    }

    @Test
    public void testFromWidthDpTablet() {
        assertEquals(ScreenSize.TABLET, ScreenSize.fromWidthDp(600));
        assertEquals(ScreenSize.TABLET, ScreenSize.fromWidthDp(660));
        assertEquals(ScreenSize.TABLET, ScreenSize.fromWidthDp(719));
    }

    @Test
    public void testFromWidthDpTV() {
        assertEquals(ScreenSize.TV, ScreenSize.fromWidthDp(720));
        assertEquals(ScreenSize.TV, ScreenSize.fromWidthDp(1000));
        assertEquals(ScreenSize.TV, ScreenSize.fromWidthDp(1920));
    }

    @Test
    public void testFromWidthDpBoundary() {
        assertEquals(ScreenSize.WATCH, ScreenSize.fromWidthDp(319));
        assertEquals(ScreenSize.PHONE, ScreenSize.fromWidthDp(320));
        assertEquals(ScreenSize.PHONE, ScreenSize.fromWidthDp(479));
        assertEquals(ScreenSize.PHABLET, ScreenSize.fromWidthDp(480));
        assertEquals(ScreenSize.PHABLET, ScreenSize.fromWidthDp(599));
        assertEquals(ScreenSize.TABLET, ScreenSize.fromWidthDp(600));
        assertEquals(ScreenSize.TABLET, ScreenSize.fromWidthDp(719));
        assertEquals(ScreenSize.TV, ScreenSize.fromWidthDp(720));
    }

    @Test
    public void testValues() {
        ScreenSize[] values = ScreenSize.values();
        assertEquals(5, values.length);
        assertEquals(ScreenSize.WATCH, values[0]);
        assertEquals(ScreenSize.PHONE, values[1]);
        assertEquals(ScreenSize.PHABLET, values[2]);
        assertEquals(ScreenSize.TABLET, values[3]);
        assertEquals(ScreenSize.TV, values[4]);
    }

    @Test
    public void testValueOf() {
        assertEquals(ScreenSize.WATCH, ScreenSize.valueOf("WATCH"));
        assertEquals(ScreenSize.PHONE, ScreenSize.valueOf("PHONE"));
        assertEquals(ScreenSize.PHABLET, ScreenSize.valueOf("PHABLET"));
        assertEquals(ScreenSize.TABLET, ScreenSize.valueOf("TABLET"));
        assertEquals(ScreenSize.TV, ScreenSize.valueOf("TV"));
    }
}
