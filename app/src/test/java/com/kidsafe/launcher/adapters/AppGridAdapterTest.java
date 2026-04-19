package com.kidsafe.launcher.adapters;

import static org.junit.Assert.*;

import com.kidsafe.launcher.models.AppInfo;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for AppListManager (underlying adapter logic).
 */
public class AppGridAdapterTest {

    private List<AppInfo> testApps;
    private AppListManager manager;

    @Before
    public void setUp() {
        testApps = new ArrayList<>(Arrays.asList(
                new AppInfo("Alpha", "com.alpha", null, null, false),
                new AppInfo("Beta", "com.beta", null, null, false),
                new AppInfo("Gamma", "com.gamma", null, null, true)
        ));
        manager = new AppListManager(testApps);
    }

    @Test
    public void testConstructorEmpty() {
        AppListManager m = new AppListManager();
        assertEquals(0, m.getCount());
        assertTrue(m.isEmpty());
    }

    @Test
    public void testConstructorWithApps() {
        assertEquals(3, manager.getCount());
        assertFalse(manager.isEmpty());
    }

    @Test
    public void testConstructorWithNullApps() {
        AppListManager m = new AppListManager(null);
        assertEquals(0, m.getCount());
    }

    @Test
    public void testUpdateApps() {
        AppListManager m = new AppListManager();
        assertEquals(0, m.getCount());

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
        assertEquals("Alpha", manager.getItem(0).getLabel());
        assertEquals("Beta", manager.getItem(1).getLabel());
        assertEquals("Gamma", manager.getItem(2).getLabel());
    }

    @Test
    public void testGetItemInvalidIndex() {
        assertNull(manager.getItem(-1));
        assertNull(manager.getItem(100));
    }

    @Test
    public void testGetItemId() {
        assertEquals(0, manager.getItemId(0));
        assertEquals(1, manager.getItemId(1));
        assertEquals(2, manager.getItemId(2));
    }

    @Test
    public void testGetAppsReturnsCopy() {
        List<AppInfo> apps = manager.getApps();
        apps.clear();
        assertEquals(3, manager.getCount()); // original not affected
    }

    @Test
    public void testUpdateAppsClears() {
        List<AppInfo> newApps = Arrays.asList(
                new AppInfo("New", "com.new", null, null, false)
        );
        manager.updateApps(newApps);
        assertEquals(1, manager.getCount());
        assertEquals("New", manager.getItem(0).getLabel());
    }

    @Test
    public void testGetCountEmpty() {
        AppListManager m = new AppListManager();
        assertEquals(0, m.getCount());
    }

    @Test
    public void testUpdateWithEmptyList() {
        manager.updateApps(new ArrayList<>());
        assertEquals(0, manager.getCount());
    }

    @Test
    public void testClear() {
        manager.clear();
        assertEquals(0, manager.getCount());
        assertTrue(manager.isEmpty());
    }

    @Test
    public void testIsEmpty() {
        assertFalse(manager.isEmpty());
        manager.clear();
        assertTrue(manager.isEmpty());
    }

    @Test
    public void testGetAppsContent() {
        List<AppInfo> apps = manager.getApps();
        assertEquals(3, apps.size());
        assertEquals("Alpha", apps.get(0).getLabel());
        assertEquals("Beta", apps.get(1).getLabel());
        assertEquals("Gamma", apps.get(2).getLabel());
    }

    @Test
    public void testMultipleUpdates() {
        manager.updateApps(Arrays.asList(new AppInfo("A", "com.a", null, null, false)));
        assertEquals(1, manager.getCount());

        manager.updateApps(Arrays.asList(
                new AppInfo("B", "com.b", null, null, false),
                new AppInfo("C", "com.c", null, null, false)
        ));
        assertEquals(2, manager.getCount());
        assertEquals("B", manager.getItem(0).getLabel());
    }

    @Test
    public void testGetItemBoundary() {
        assertNotNull(manager.getItem(0));
        assertNotNull(manager.getItem(2));
        assertNull(manager.getItem(3));
    }
}
