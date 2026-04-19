package com.kidsafe.launcher.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Utility class for network and connectivity operations.
 */
public class NetworkUtils {

    private NetworkUtils() {
        // Prevent instantiation
    }

    /**
     * Check if WiFi is enabled.
     */
    public static boolean isWifiEnabled(Context context) {
        WifiManager wifiManager = getWifiManager(context);
        return wifiManager != null && wifiManager.isWifiEnabled();
    }

    /**
     * Set WiFi enabled/disabled.
     */
    @SuppressWarnings("deprecation")
    public static boolean setWifiEnabled(Context context, boolean enabled) {
        WifiManager wifiManager = getWifiManager(context);
        if (wifiManager != null) {
            try {
                return wifiManager.setWifiEnabled(enabled);
            } catch (SecurityException e) {
                // On newer Android, open WiFi settings instead
                openWifiSettings(context);
                return false;
            }
        }
        return false;
    }

    /**
     * Get current WiFi connection name (SSID).
     */
    @SuppressWarnings("deprecation")
    public static String getCurrentWifiSsid(Context context) {
        WifiManager wifiManager = getWifiManager(context);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                String ssid = wifiInfo.getSSID();
                if (ssid != null && !ssid.equals("<unknown ssid>")) {
                    return ssid.replace("\"", "");
                }
            }
        }
        return null;
    }

    /**
     * Open system WiFi settings.
     */
    public static void openWifiSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Open system Bluetooth settings.
     */
    public static void openBluetoothSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Open wired network (Ethernet) settings.
     */
    public static void openNetworkSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Check if Bluetooth is enabled.
     */
    public static boolean isBluetoothEnabled() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        return adapter != null && adapter.isEnabled();
    }

    /**
     * Get list of paired Bluetooth devices.
     */
    public static List<String> getPairedBluetoothDevices() {
        List<String> devices = new ArrayList<>();
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            try {
                Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
                for (BluetoothDevice device : pairedDevices) {
                    String name = device.getName();
                    devices.add(name != null ? name : device.getAddress());
                }
            } catch (SecurityException e) {
                // Permission not granted
            }
        }
        return devices;
    }

    /**
     * Check if device is connected to the internet.
     */
    @SuppressWarnings("deprecation")
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    /**
     * Get connection type string.
     */
    @SuppressWarnings("deprecation")
    public static String getConnectionType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected()) {
                switch (activeNetwork.getType()) {
                    case ConnectivityManager.TYPE_WIFI:
                        return "WiFi";
                    case ConnectivityManager.TYPE_MOBILE:
                        return "Mobile Data";
                    case ConnectivityManager.TYPE_ETHERNET:
                        return "Ethernet";
                    default:
                        return "Other";
                }
            }
        }
        return "Disconnected";
    }

    private static WifiManager getWifiManager(Context context) {
        return (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }
}
