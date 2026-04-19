package com.kidsafe.launcher.adapters;

import static org.junit.Assert.*;

import com.kidsafe.launcher.models.AppInfo;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for AppListManager (manage adapter logic).
 */
public class AppManageAdapterTest {

    private List<AppInfo> testApps;
    private AppListManager manager;

    @Before
    public void setUp() {
        testApps = new ArrayList<>(Arrays.asList(
                new AppInfo("User App", "com.user", null, null, false),
                new AppInfo("System App", "com.system", null, null, true),
                new AppInfo("Another App", "com.another", null, null, false)
        ));
        manager = new AppListManager(testApps);
    }

    @Test
    public void testConstructorEmpty() {
        AppListManager m = new AppListManager();
        assertEquals(0, m.getCount());
    }

    @Test
    public void testConstructorWithApps() {
        assertEquals(3, manager.getCount());
    }

    @Test
    public void testConstructorWithNullApps() {
        AppListManager m = new AppListManager(null);
        assertEquals(0, m.getCount());
    }

    @Test
    public void testUpdateApps() {
        AppListManager m = new AppListManager();
        m.updateApps(testApps);
        assertEquals(3, m.getCount());
    }

    @Test
    public void testUpdateAppsWithNull() {
        manager.updateApps(null);
        assertEquals(0, manager.getCount());
    }

    @Test
    public void testGetItem() {
        assertEquals("User App", manager.getItem(0).getLabel());
        assertEquals("System App", manager.getItem(1).getLabel());
    }

    @Test
    public void testGetItemId() {
        assertEquals(0, manager.getItemId(0));
        assertEquals(1, manager.getItemId(1));
    }

    @Test
    public void testGetAppsReturnsCopy() {
        List<AppInfo> apps = manager.getApps();
        apps.clear();
        assertEquals(3, manager.getCount());
    }

    @Test
    public void testUpdateAppsClears() {
        List<AppInfo> newApps = Arrays.asList(
                new AppInfo("Only", "com.only", null, null, false)
        );
        manager.updateApps(newApps);
        assertEquals(1, manager.getCount());
        assertEquals("Only", manager.getItem(0).getLabel());
    }

    @Test
    public void testEmptyAfterClear() {
        manager.updateApps(new ArrayList<>());
        assertEquals(0, manager.getCount());
    }

    @Test
    public void testGetAppsContent() {
        List<AppInfo> apps = manager.getApps();
        assertEquals(3, apps.size());
        assertEquals("User App", apps.get(0).getLabel());
        assertEquals("System App", apps.get(1).getLabel());
        assertEquals("Another App", apps.get(2).getLabel());
    }

    @Test
    public void testSystemAppFlag() {
        AppInfo sysApp = manager.getItem(1);
        assertTrue(sysApp.isSystemApp());

        AppInfo userApp = manager.getItem(0);
        assertFalse(userApp.isSystemApp());
    }

    @Test
    public void testClearAndRefill() {
        manager.clear();
        assertEquals(0, manager.getCount());

        manager.updateApps(testApps);
        assertEquals(3, manager.getCount());
    }

    @Test
    public void testGetItemInvalidIndex() {
        assertNull(manager.getItem(-1));
        assertNull(manager.getItem(999));
    }
}
