package com.kidsafe.launcher.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages parental control settings including PIN protection and app visibility.
 * Uses SharedPreferences for persistent storage.
 */
public class ParentalControlManager {

    private static final String PREFS_NAME = "kid_safe_parental";
    private static final String KEY_PIN_HASH = "pin_hash";
    private static final String KEY_HIDDEN_APPS = "hidden_apps";
    private static final String KEY_VIEW_MODE = "view_mode";
    private static final String DEFAULT_PIN = "0000";
    private static final String SALT = "KidSafeLauncher2024";

    public static final int VIEW_MODE_GRID = 0;
    public static final int VIEW_MODE_LIST = 1;

    private final SharedPreferences prefs;

    public ParentalControlManager(Context context) {
        this.prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Check if PIN has been set by user (not default).
     */
    public boolean isPinConfigured() {
        return prefs.contains(KEY_PIN_HASH);
    }

    /**
     * Verify the entered PIN against stored PIN.
     */
    public boolean verifyPin(String pin) {
        if (pin == null) return false;
        String stored = prefs.getString(KEY_PIN_HASH, hashPin(DEFAULT_PIN));
        return stored.equals(hashPin(pin));
    }

    /**
     * Set a new PIN (4-8 digits).
     */
    public void setPin(String pin) {
        if (pin == null || pin.length() < 4 || pin.length() > 8) return;
        prefs.edit().putString(KEY_PIN_HASH, hashPin(pin)).apply();
    }

    /**
     * Get the set of hidden app package names.
     */
    public Set<String> getHiddenApps() {
        return new HashSet<>(prefs.getStringSet(KEY_HIDDEN_APPS, new HashSet<>()));
    }

    /**
     * Set the hidden apps list.
     */
    public void setHiddenApps(Set<String> packageNames) {
        prefs.edit().putStringSet(KEY_HIDDEN_APPS, packageNames).apply();
    }

    /**
     * Check if an app is hidden.
     */
    public boolean isAppHidden(String packageName) {
        return getHiddenApps().contains(packageName);
    }

    /**
     * Toggle visibility for an app.
     */
    public void toggleAppVisibility(String packageName) {
        Set<String> hidden = getHiddenApps();
        if (hidden.contains(packageName)) {
            hidden.remove(packageName);
        } else {
            hidden.add(packageName);
        }
        setHiddenApps(hidden);
    }

    /**
     * Get the current view mode (grid or list).
     */
    public int getViewMode() {
        return prefs.getInt(KEY_VIEW_MODE, VIEW_MODE_GRID);
    }

    /**
     * Set the view mode.
     */
    public void setViewMode(int mode) {
        prefs.edit().putInt(KEY_VIEW_MODE, mode).apply();
    }

    /**
     * Get count of hidden apps.
     */
    public int getHiddenAppsCount() {
        return getHiddenApps().size();
    }

    /**
     * Hash PIN using SHA-256 with salt for secure storage.
     * Falls back to a simpler hash if SHA-256 is unavailable.
     */
    static String hashPin(String pin) {
        if (pin == null) return "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((SALT + pin).getBytes());
            StringBuilder sb = new StringBuilder("ph_");
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // Fallback: simple hash if SHA-256 not available
            int hash = 7;
            String salted = SALT + pin;
            for (int i = 0; i < salted.length(); i++) {
                hash = hash * 31 + salted.charAt(i);
            }
            return "ph_" + Integer.toHexString(hash);
        }
    }
}
