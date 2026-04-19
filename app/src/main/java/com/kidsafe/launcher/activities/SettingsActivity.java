package com.kidsafe.launcher.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import com.kidsafe.launcher.R;
import com.kidsafe.launcher.utils.NetworkUtils;

/**
 * Settings activity for managing WiFi, Bluetooth, Network, and other device settings.
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupToolbar();
        setupSettingsItems();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setActionBar(toolbar);
            if (getActionBar() != null) {
                getActionBar().setDisplayHomeAsUpEnabled(true);
                getActionBar().setTitle(R.string.settings);
            }
        }
    }

    private void setupSettingsItems() {
        LinearLayout wifiItem = findViewById(R.id.settings_wifi);
        LinearLayout bluetoothItem = findViewById(R.id.settings_bluetooth);
        LinearLayout networkItem = findViewById(R.id.settings_network);
        LinearLayout hardwareItem = findViewById(R.id.settings_hardware);
        LinearLayout manageAppsItem = findViewById(R.id.settings_manage_apps);

        if (wifiItem != null) {
            wifiItem.setOnClickListener(v -> NetworkUtils.openWifiSettings(this));
        }

        if (bluetoothItem != null) {
            bluetoothItem.setOnClickListener(v -> NetworkUtils.openBluetoothSettings(this));
        }

        if (networkItem != null) {
            networkItem.setOnClickListener(v -> NetworkUtils.openNetworkSettings(this));
        }

        if (hardwareItem != null) {
            hardwareItem.setOnClickListener(v -> {
                startActivity(new Intent(this, HardwareInfoActivity.class));
            });
        }

        if (manageAppsItem != null) {
            manageAppsItem.setOnClickListener(v -> {
                startActivity(new Intent(this, AppManageActivity.class));
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
