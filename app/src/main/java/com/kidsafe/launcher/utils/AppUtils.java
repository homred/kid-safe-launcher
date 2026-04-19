package com.kidsafe.launcher.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.kidsafe.launcher.models.AppInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for managing installed applications.
 */
public class AppUtils {

    private AppUtils() {
        // Prevent instantiation
    }

    /**
     * Get list of all launchable apps installed on the device.
     */
    public static List<AppInfo> getInstalledApps(Context context) {
        List<AppInfo> apps = new ArrayList<>();
        PackageManager pm = context.getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent, 0);

        String ownPackage = context.getPackageName();

        for (ResolveInfo ri : resolveInfos) {
            String packageName = ri.activityInfo.packageName;

            // Skip our own launcher
            if (packageName.equals(ownPackage)) {
                continue;
            }

            String label = ri.loadLabel(pm).toString();
            android.content.ComponentName componentName = new android.content.ComponentName(
                    ri.activityInfo.packageName,
                    ri.activityInfo.name
            );
            android.graphics.drawable.Drawable icon = ri.loadIcon(pm);
            boolean isSystem = (ri.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

            apps.add(new AppInfo(label, packageName, componentName, icon, isSystem));
        }

        Collections.sort(apps);
        return apps;
    }

    /**
     * Launch an application by its ComponentName.
     */
    public static boolean launchApp(Context context, android.content.ComponentName componentName) {
        if (context == null || componentName == null) return false;
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setComponent(componentName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Uninstall an application.
     */
    public static void requestUninstall(Context context, String packageName) {
        if (context == null || packageName == null) return;
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Open app details in system settings.
     */
    public static void openAppDetails(Context context, String packageName) {
        if (context == null || packageName == null) return;
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Filter apps by search query.
     */
    public static List<AppInfo> filterApps(List<AppInfo> apps, String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(apps);
        }
        String lowerQuery = query.toLowerCase().trim();
        List<AppInfo> filtered = new ArrayList<>();
        for (AppInfo app : apps) {
            if (app.getLabel().toLowerCase().contains(lowerQuery) ||
                    app.getPackageName().toLowerCase().contains(lowerQuery)) {
                filtered.add(app);
            }
        }
        return filtered;
    }

    /**
     * Separate system apps from user-installed apps.
     */
    public static List<AppInfo> getUserApps(List<AppInfo> apps) {
        List<AppInfo> userApps = new ArrayList<>();
        for (AppInfo app : apps) {
            if (!app.isSystemApp()) {
                userApps.add(app);
            }
        }
        return userApps;
    }

    /**
     * Get only system apps.
     */
    public static List<AppInfo> getSystemApps(List<AppInfo> apps) {
        List<AppInfo> systemApps = new ArrayList<>();
        for (AppInfo app : apps) {
            if (app.isSystemApp()) {
                systemApps.add(app);
            }
        }
        return systemApps;
    }
}
