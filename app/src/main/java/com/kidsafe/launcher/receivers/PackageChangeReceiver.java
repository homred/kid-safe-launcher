package com.kidsafe.launcher.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcast receiver for detecting package install/uninstall/change events.
 */
public class PackageChangeReceiver extends BroadcastReceiver {

    private OnPackageChangeListener listener;

    public interface OnPackageChangeListener {
        void onPackageChanged();
    }

    public void setOnPackageChangeListener(OnPackageChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        String action = intent.getAction();
        if (Intent.ACTION_PACKAGE_ADDED.equals(action) ||
                Intent.ACTION_PACKAGE_REMOVED.equals(action) ||
                Intent.ACTION_PACKAGE_CHANGED.equals(action)) {
            if (listener != null) {
                listener.onPackageChanged();
            }
        }
    }
}
