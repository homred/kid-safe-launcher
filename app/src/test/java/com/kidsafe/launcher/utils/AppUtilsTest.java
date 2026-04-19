package com.kidsafe.launcher.utils;

import static org.junit.Assert.*;

import com.kidsafe.launcher.models.AppInfo;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Unit tests for AppUtils utility class.
 */
public class AppUtilsTest {

    private List<AppInfo> testApps;

    @Before
    public void setUp() {
        testApps = new ArrayList<>();
        testApps.add(new AppInfo("Calculator", "com.android.calc", null, null, true));
        testApps.add(new AppInfo("My App", "com.example.myapp", null, null, false));
        testApps.add(new AppInfo("Browser", "com.android.browser", null, null, true));
        testApps.add(new AppInfo("Game", "com.example.game", null, null, false));
        testApps.add(new AppInfo("Settings", "com.android.settings", null, null, true));
    }

    @Test
    public void testFilterAppsWithEmptyQuery() {
        List<AppInfo> result = AppUtils.filterApps(testApps, "");
        assertEquals(5, result.size());
    }

    @Test
    public void testFilterAppsWithNullQuery() {
        List<AppInfo> result = AppUtils.filterApps(testApps, null);
        assertEquals(5, result.size());
    }

    @Test
    public void testFilterAppsWithWhitespaceQuery() {
        List<AppInfo> result = AppUtils.filterApps(testApps, "   ");
        assertEquals(5, result.size());
    }

    @Test
    public void testFilterAppsByLabel() {
        List<AppInfo> result = AppUtils.filterApps(testApps, "calc");
        assertEquals(1, result.size());
        assertEquals("Calculator", result.get(0).getLabel());
    }

    @Test
    public void testFilterAppsByLabelCaseInsensitive() {
        List<AppInfo> result = AppUtils.filterApps(testApps, "CALC");
        assertEquals(1, result.size());
        assertEquals("Calculator", result.get(0).getLabel());
    }

    @Test
    public void testFilterAppsByPackageName() {
        List<AppInfo> result = AppUtils.filterApps(testApps, "com.example");
        assertEquals(2, result.size());
    }

    @Test
    public void testFilterAppsNoMatch() {
        List<AppInfo> result = AppUtils.filterApps(testApps, "nonexistent");
        assertEquals(0, result.size());
    }

    @Test
    public void testFilterAppsPartialMatch() {
        List<AppInfo> result = AppUtils.filterApps(testApps, "brow");
        assertEquals(1, result.size());
        assertEquals("Browser", result.get(0).getLabel());
    }

    @Test
    public void testGetUserApps() {
        List<AppInfo> userApps = AppUtils.getUserApps(testApps);
        assertEquals(2, userApps.size());
        for (AppInfo app : userApps) {
            assertFalse(app.isSystemApp());
        }
    }

    @Test
    public void testGetSystemApps() {
        List<AppInfo> systemApps = AppUtils.getSystemApps(testApps);
        assertEquals(3, systemApps.size());
        for (AppInfo app : systemApps) {
            assertTrue(app.isSystemApp());
        }
    }

    @Test
    public void testGetUserAppsEmpty() {
        List<AppInfo> allSystem = Arrays.asList(
                new AppInfo("A", "com.a", null, null, true),
                new AppInfo("B", "com.b", null, null, true)
        );
        List<AppInfo> result = AppUtils.getUserApps(allSystem);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetSystemAppsEmpty() {
        List<AppInfo> allUser = Arrays.asList(
                new AppInfo("A", "com.a", null, null, false),
                new AppInfo("B", "com.b", null, null, false)
        );
        List<AppInfo> result = AppUtils.getSystemApps(allUser);
        assertEquals(0, result.size());
    }

    @Test
    public void testFilterAppsReturnsCopy() {
        List<AppInfo> result = AppUtils.filterApps(testApps, "");
        result.clear();
        assertEquals(5, testApps.size()); // original not affected
    }

    @Test
    public void testLaunchAppWithNullContext() {
        boolean result = AppUtils.launchApp(null, null);
        assertFalse(result);
    }

    @Test
    public void testFilterAppsEmptyList() {
        List<AppInfo> result = AppUtils.filterApps(new ArrayList<>(), "query");
        assertEquals(0, result.size());
    }

    @Test
    public void testGetUserAppsEmptyList() {
        List<AppInfo> result = AppUtils.getUserApps(new ArrayList<>());
        assertEquals(0, result.size());
    }

    @Test
    public void testGetSystemAppsEmptyList() {
        List<AppInfo> result = AppUtils.getSystemApps(new ArrayList<>());
        assertEquals(0, result.size());
    }

    @Test
    public void testFilterAppsMultipleMatches() {
        List<AppInfo> result = AppUtils.filterApps(testApps, "com.android");
        assertEquals(3, result.size());
    }

    @Test
    public void testFilterAppsSingleCharQuery() {
        // "G" matches "Game" (label) and "Settings" (label contains 'g' case-insensitive)
        List<AppInfo> result = AppUtils.filterApps(testApps, "G");
        assertEquals(2, result.size());
    }

    @Test
    public void testFilterAppsPackageNameMatch() {
        List<AppInfo> result = AppUtils.filterApps(testApps, "settings");
        assertEquals(1, result.size());
    }
}
