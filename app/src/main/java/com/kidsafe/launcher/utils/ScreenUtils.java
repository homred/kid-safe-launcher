package com.kidsafe.launcher.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.kidsafe.launcher.models.ScreenSize;

/**
 * Utility class for screen size detection and adaptive layout management.
 */
public class ScreenUtils {

    private ScreenUtils() {
        // Prevent instantiation
    }

    /**
     * Detect the current screen size category.
     */
    public static ScreenSize getScreenSize(Context context) {
        int widthDp = getScreenWidthDp(context);
        return ScreenSize.fromWidthDp(widthDp);
    }

    /**
     * Get screen width in dp.
     */
    public static int getScreenWidthDp(Context context) {
        Configuration config = context.getResources().getConfiguration();
        return config.smallestScreenWidthDp;
    }

    /**
     * Get screen width in pixels.
     */
    public static int getScreenWidthPx(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            return dm.widthPixels;
        }
        return 0;
    }

    /**
     * Get screen height in pixels.
     */
    public static int getScreenHeightPx(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            return dm.heightPixels;
        }
        return 0;
    }

    /**
     * Check if the device is in landscape orientation.
     */
    public static boolean isLandscape(Context context) {
        Configuration config = context.getResources().getConfiguration();
        return config.orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * Get the number of columns for the app grid based on screen size.
     */
    public static int getGridColumnCount(Context context) {
        ScreenSize screenSize = getScreenSize(context);
        if (isLandscape(context)) {
            return screenSize.getGridColumns() + 1;
        }
        return screenSize.getGridColumns();
    }

    /**
     * Check if status bar should be shown.
     */
    public static boolean shouldShowStatusBar(Context context) {
        return getScreenSize(context).shouldShowStatusBar();
    }

    /**
     * Convert dp to pixels.
     */
    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    /**
     * Convert pixels to dp.
     */
    public static int pxToDp(Context context, int px) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(px / density);
    }

    /**
     * Check if running on a TV device.
     */
    public static boolean isTv(Context context) {
        return context.getPackageManager().hasSystemFeature("android.software.leanback");
    }

    /**
     * Check if running on a watch device.
     */
    public static boolean isWatch(Context context) {
        return context.getPackageManager().hasSystemFeature("android.hardware.type.watch");
    }
}
