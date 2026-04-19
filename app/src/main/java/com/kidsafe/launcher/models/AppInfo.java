package com.kidsafe.launcher.models;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;

/**
 * Model representing an installed application.
 */
public class AppInfo implements Comparable<AppInfo> {
    private final String label;
    private final String packageName;
    private final ComponentName componentName;
    private final Drawable icon;
    private final boolean isSystemApp;

    public AppInfo(String label, String packageName, ComponentName componentName,
                   Drawable icon, boolean isSystemApp) {
        this.label = label != null ? label : "";
        this.packageName = packageName != null ? packageName : "";
        this.componentName = componentName;
        this.icon = icon;
        this.isSystemApp = isSystemApp;
    }

    public String getLabel() {
        return label;
    }

    public String getPackageName() {
        return packageName;
    }

    public ComponentName getComponentName() {
        return componentName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public boolean isSystemApp() {
        return isSystemApp;
    }

    @Override
    public int compareTo(AppInfo other) {
        return this.label.compareToIgnoreCase(other.label);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppInfo appInfo = (AppInfo) o;
        return packageName.equals(appInfo.packageName);
    }

    @Override
    public int hashCode() {
        return packageName.hashCode();
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "label='" + label + '\'' +
                ", packageName='" + packageName + '\'' +
                ", isSystemApp=" + isSystemApp +
                '}';
    }
}
