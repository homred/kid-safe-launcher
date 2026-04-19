package com.kidsafe.launcher.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.kidsafe.launcher.R;
import com.kidsafe.launcher.models.DeviceInfo;
import com.kidsafe.launcher.utils.DeviceUtils;

/**
 * Activity displaying detailed hardware information about the device.
 */
public class HardwareInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hardware_info);

        setupToolbar();
        displayHardwareInfo();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle(R.string.hardware_info);
            }
        }
    }

    private void displayHardwareInfo() {
        DeviceInfo info = DeviceUtils.getDeviceInfo(this);

        setText(R.id.info_device_name, info.getDeviceName());
        setText(R.id.info_manufacturer, info.getManufacturer());
        setText(R.id.info_model, info.getModel());
        setText(R.id.info_android_version, info.getAndroidVersion());
        setText(R.id.info_sdk_version, String.valueOf(info.getSdkVersion()));
        setText(R.id.info_cpu, info.getCpuArchitecture());
        setText(R.id.info_ram, info.getFormattedRam());
        setText(R.id.info_storage, info.getFormattedStorage());
        setText(R.id.info_screen, info.getScreenResolution());
        setText(R.id.info_density, String.format("%.1f", info.getScreenDensity()));
        setText(R.id.info_battery, info.getBatteryLevel() + "%");
    }

    private void setText(int viewId, String text) {
        TextView tv = findViewById(viewId);
        if (tv != null) {
            tv.setText(text);
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
