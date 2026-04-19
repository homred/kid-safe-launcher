package com.kidsafe.launcher.adapters;

import com.kidsafe.launcher.models.AppInfo;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for AppListAdapter data management (non-Android parts).
 * Since AppListAdapter extends BaseAdapter, we test the data logic
 * similar to how AppGridAdapterTest works.
 */
public class AppListAdapterTest {

    private AppListManager listManager;

    @Before
    public void setUp() {
        listManager = new AppListManager();
    }

    @Test
    public void testInitialCountIsZero() {
        assertEquals(0, listManager.getCount());
    }

    @Test
    public void testUpdateAppsUpdatesCount() {
        List<AppInfo> apps = createTestApps(5);
        listManager.updateApps(apps);
        assertEquals(5, listManager.getCount());
    }

    @Test
    public void testGetItemReturnsCorrectApp() {
        List<AppInfo> apps = createTestApps(3);
        listManager.updateApps(apps);
        AppInfo item = listManager.getItem(1);
        assertNotNull(item);
        assertEquals("App 1", item.getLabel());
    }

    @Test
    public void testGetItemOutOfBoundsReturnsNull() {
        List<AppInfo> apps = createTestApps(2);
        listManager.updateApps(apps);
        assertNull(listManager.getItem(5));
    }

    @Test
    public void testGetItemNegativeIndexReturnsNull() {
        List<AppInfo> apps = createTestApps(2);
        listManager.updateApps(apps);
        assertNull(listManager.getItem(-1));
    }

    @Test
    public void testUpdateAppsWithNull() {
        listManager.updateApps(null);
        assertEquals(0, listManager.getCount());
    }

    @Test
    public void testUpdateAppsWithEmpty() {
        listManager.updateApps(new ArrayList<>());
        assertEquals(0, listManager.getCount());
    }

    @Test
    public void testClear() {
        listManager.updateApps(createTestApps(3));
        listManager.clear();
        assertEquals(0, listManager.getCount());
    }

    @Test
    public void testIsEmpty() {
        assertTrue(listManager.isEmpty());
        listManager.updateApps(createTestApps(1));
        assertFalse(listManager.isEmpty());
    }

    @Test
    public void testGetAppsReturnsCopy() {
        List<AppInfo> apps = createTestApps(3);
        listManager.updateApps(apps);
        List<AppInfo> retrieved = listManager.getApps();
        assertEquals(3, retrieved.size());
        // Modifying returned list shouldn't affect internal
        retrieved.clear();
        assertEquals(3, listManager.getCount());
    }

    @Test
    public void testGetItemId() {
        listManager.updateApps(createTestApps(3));
        assertEquals(0, listManager.getItemId(0));
        assertEquals(1, listManager.getItemId(1));
        assertEquals(2, listManager.getItemId(2));
    }

    private List<AppInfo> createTestApps(int count) {
        List<AppInfo> apps = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            apps.add(new AppInfo("App " + i, "com.test.app" + i, null, null, i % 2 == 0));
        }
        return apps;
    }
}
