package com.kidsafe.launcher.adapters;

import com.kidsafe.launcher.models.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the app list data independent of Android views.
 * This class can be tested without Android framework dependencies.
 */
public class AppListManager {

    private final List<AppInfo> apps;

    public AppListManager() {
        this.apps = new ArrayList<>();
    }

    public AppListManager(List<AppInfo> initialApps) {
        this.apps = initialApps != null ? new ArrayList<>(initialApps) : new ArrayList<>();
    }

    public void updateApps(List<AppInfo> newApps) {
        apps.clear();
        if (newApps != null) {
            apps.addAll(newApps);
        }
    }

    public int getCount() {
        return apps.size();
    }

    public AppInfo getItem(int position) {
        if (position < 0 || position >= apps.size()) {
            return null;
        }
        return apps.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public List<AppInfo> getApps() {
        return new ArrayList<>(apps);
    }

    public boolean isEmpty() {
        return apps.isEmpty();
    }

    public void clear() {
        apps.clear();
    }
}
