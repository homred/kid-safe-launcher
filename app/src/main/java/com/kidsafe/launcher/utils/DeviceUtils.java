package com.kidsafe.launcher.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.kidsafe.launcher.models.DeviceInfo;

/**
 * Utility class for retrieving device hardware information.
 */
public class DeviceUtils {

    private DeviceUtils() {
        // Prevent instantiation
    }

    /**
     * Gather all device hardware information.
     */
    public static DeviceInfo getDeviceInfo(Context context) {
        DeviceInfo.Builder builder = new DeviceInfo.Builder();

        builder.setDeviceName(Build.DEVICE)
                .setManufacturer(Build.MANUFACTURER)
                .setModel(Build.MODEL)
                .setAndroidVersion(Build.VERSION.RELEASE)
                .setSdkVersion(Build.VERSION.SDK_INT)
                .setCpuArchitecture(getCpuArchitecture());

        // RAM info
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(memInfo);
            builder.setTotalRam(memInfo.totalMem)
                    .setAvailableRam(memInfo.availMem);
        }

        // Storage info
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        long totalBytes = stat.getTotalBytes();
        long availBytes = stat.getAvailableBytes();
        builder.setTotalStorage(totalBytes)
                .setAvailableStorage(availBytes);

        // Screen info
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            builder.setScreenWidth(dm.widthPixels)
                    .setScreenHeight(dm.heightPixels)
                    .setScreenDensity(dm.density);
        }

        // Battery
        builder.setBatteryLevel(getBatteryLevel(context));

        // Serial
        builder.setSerialNumber(getSerialNumber());

        return builder.build();
    }

    /**
     * Get the current battery level percentage.
     */
    public static int getBatteryLevel(Context context) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, filter);
        if (batteryStatus != null) {
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            if (level >= 0 && scale > 0) {
                return (int) ((level / (float) scale) * 100);
            }
        }
        return -1;
    }

    /**
     * Get CPU architecture string.
     */
    public static String getCpuArchitecture() {
        String[] abis = Build.SUPPORTED_ABIS;
        if (abis != null && abis.length > 0) {
            return abis[0];
        }
        return "Unknown";
    }

    /**
     * Get device serial number (may require permission).
     */
    @SuppressWarnings("deprecation")
    public static String getSerialNumber() {
        try {
            return Build.SERIAL;
        } catch (SecurityException e) {
            return "N/A";
        }
    }
}
