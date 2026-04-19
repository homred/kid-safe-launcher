package com.kidsafe.launcher.models;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit tests for AppInfo model.
 */
public class AppInfoTest {

    @Test
    public void testConstructor() {
        AppInfo app = new AppInfo("Test App", "com.test.app", null, null, false);
        assertEquals("Test App", app.getLabel());
        assertEquals("com.test.app", app.getPackageName());
        assertNull(app.getComponentName());
        assertNull(app.getIcon());
        assertFalse(app.isSystemApp());
    }

    @Test
    public void testConstructorWithNulls() {
        AppInfo app = new AppInfo(null, null, null, null, true);
        assertEquals("", app.getLabel());
        assertEquals("", app.getPackageName());
        assertTrue(app.isSystemApp());
    }

    @Test
    public void testSystemApp() {
        AppInfo systemApp = new AppInfo("System", "com.android.system", null, null, true);
        assertTrue(systemApp.isSystemApp());

        AppInfo userApp = new AppInfo("User", "com.user.app", null, null, false);
        assertFalse(userApp.isSystemApp());
    }

    @Test
    public void testCompareTo() {
        AppInfo app1 = new AppInfo("Alpha", "com.alpha", null, null, false);
        AppInfo app2 = new AppInfo("Beta", "com.beta", null, null, false);
        AppInfo app3 = new AppInfo("alpha", "com.alpha2", null, null, false);

        assertTrue(app1.compareTo(app2) < 0);
        assertTrue(app2.compareTo(app1) > 0);
        assertEquals(0, app1.compareTo(app3)); // case insensitive
    }

    @Test
    public void testEquals() {
        AppInfo app1 = new AppInfo("App", "com.test.app", null, null, false);
        AppInfo app2 = new AppInfo("Different Name", "com.test.app", null, null, true);
        AppInfo app3 = new AppInfo("App", "com.other.app", null, null, false);

        assertEquals(app1, app2); // same package name
        assertNotEquals(app1, app3); // different package name
        assertNotEquals(app1, null);
        assertNotEquals(app1, "string");
        assertEquals(app1, app1); // same reference
    }

    @Test
    public void testHashCode() {
        AppInfo app1 = new AppInfo("App", "com.test.app", null, null, false);
        AppInfo app2 = new AppInfo("Different", "com.test.app", null, null, true);

        assertEquals(app1.hashCode(), app2.hashCode());
    }

    @Test
    public void testToString() {
        AppInfo app = new AppInfo("My App", "com.my.app", null, null, false);
        String str = app.toString();

        assertTrue(str.contains("My App"));
        assertTrue(str.contains("com.my.app"));
        assertTrue(str.contains("false"));
    }

    @Test
    public void testGetLabel() {
        AppInfo app = new AppInfo("Test Label", "com.test", null, null, false);
        assertEquals("Test Label", app.getLabel());
    }

    @Test
    public void testGetPackageName() {
        AppInfo app = new AppInfo("Test", "com.example.test", null, null, false);
        assertEquals("com.example.test", app.getPackageName());
    }

    @Test
    public void testCompareToSameLabel() {
        AppInfo app1 = new AppInfo("Same", "com.pkg1", null, null, false);
        AppInfo app2 = new AppInfo("Same", "com.pkg2", null, null, false);
        assertEquals(0, app1.compareTo(app2));
    }

    @Test
    public void testEqualsWithDifferentClass() {
        AppInfo app = new AppInfo("App", "com.test", null, null, false);
        assertNotEquals(app, 42);
        assertNotEquals(app, new Object());
    }
}
