package com.kidsafe.launcher.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import com.kidsafe.launcher.R;
import com.kidsafe.launcher.utils.NetworkUtils;
import com.kidsafe.launcher.utils.ParentalControlManager;
import com.kidsafe.launcher.utils.ScreenUtils;

/**
 * Settings activity for managing WiFi, Bluetooth, Network, parental controls
 * and other device settings. Dark themed to match launcher.
 */
public class SettingsActivity extends Activity {

    private ParentalControlManager controlManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        controlManager = new ParentalControlManager(this);

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
        LinearLayout parentalItem = findViewById(R.id.settings_parental);
        LinearLayout changePinItem = findViewById(R.id.settings_change_pin);

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
                promptPinForAction(() -> {
                    startActivity(new Intent(this, AppManageActivity.class));
                });
            });
        }

        if (parentalItem != null) {
            parentalItem.setOnClickListener(v -> {
                promptPinForAction(() -> {
                    startActivity(new Intent(this, ParentalControlActivity.class));
                });
            });
        }

        if (changePinItem != null) {
            changePinItem.setOnClickListener(v -> {
                promptPinForAction(this::showChangePinDialog);
            });
        }
    }

    private void promptPinForAction(Runnable action) {
        final EditText pinInput = new EditText(this);
        pinInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        pinInput.setHint(R.string.enter_pin);
        pinInput.setTextColor(getColor(R.color.text_primary));
        pinInput.setHintTextColor(getColor(R.color.text_hint));

        int pad = ScreenUtils.dpToPx(this, 24);
        LinearLayout container = new LinearLayout(this);
        container.setPadding(pad, pad, pad, 0);
        container.addView(pinInput, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog)
                .setTitle(R.string.pin_required)
                .setView(container)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    String pin = pinInput.getText().toString();
                    if (controlManager.verifyPin(pin)) {
                        action.run();
                    } else {
                        new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog)
                                .setMessage(R.string.wrong_pin)
                                .setPositiveButton(R.string.ok, null)
                                .show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showChangePinDialog() {
        final EditText newPinInput = new EditText(this);
        newPinInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        newPinInput.setHint(R.string.new_pin);
        newPinInput.setTextColor(getColor(R.color.text_primary));
        newPinInput.setHintTextColor(getColor(R.color.text_hint));

        final EditText confirmInput = new EditText(this);
        confirmInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        confirmInput.setHint(R.string.confirm_pin);
        confirmInput.setTextColor(getColor(R.color.text_primary));
        confirmInput.setHintTextColor(getColor(R.color.text_hint));

        int pad = ScreenUtils.dpToPx(this, 24);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(pad, pad, pad, 0);
        container.addView(newPinInput, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        container.addView(confirmInput, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog)
                .setTitle(R.string.change_pin)
                .setView(container)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String newPin = newPinInput.getText().toString();
                    String confirmPin = confirmInput.getText().toString();
                    if (newPin.length() >= 4 && newPin.equals(confirmPin)) {
                        controlManager.setPin(newPin);
                        new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog)
                                .setMessage(R.string.pin_set_success)
                                .setPositiveButton(R.string.ok, null)
                                .show();
                    } else {
                        new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog)
                                .setMessage(R.string.pin_mismatch)
                                .setPositiveButton(R.string.ok, null)
                                .show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
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
