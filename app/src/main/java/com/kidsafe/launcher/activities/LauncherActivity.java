package com.kidsafe.launcher.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kidsafe.launcher.R;
import com.kidsafe.launcher.adapters.AppGridAdapter;
import com.kidsafe.launcher.adapters.AppListAdapter;
import com.kidsafe.launcher.models.AppInfo;
import com.kidsafe.launcher.models.ScreenSize;
import com.kidsafe.launcher.receivers.PackageChangeReceiver;
import com.kidsafe.launcher.utils.AppUtils;
import com.kidsafe.launcher.utils.DeviceUtils;
import com.kidsafe.launcher.utils.NetworkUtils;
import com.kidsafe.launcher.utils.ParentalControlManager;
import com.kidsafe.launcher.utils.ScreenUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main launcher activity that replaces the device home screen.
 * Supports adaptive layout for watch, phone, tablet, and TV screens.
 * Features: dark PS5-style theme, async loading, grid/list toggle,
 * parental controls, enhanced quick settings, TV side panel.
 */
public class LauncherActivity extends Activity {

    private GridView appGrid;
    private ListView appList;
    private AppGridAdapter appAdapter;
    private AppListAdapter appListAdapter;
    private EditText searchBox;
    private View statusBar;
    private TextView clockText;
    private TextView dateText;
    private TextView greetingText;
    private TextView connectionText;
    private TextView batteryText;
    private View quickPanel;
    private View quickPanelOverlay;
    private LinearLayout quickPanelContent;
    private LinearLayout sidePanel;
    private TextView btnGridView;
    private TextView btnListView;

    private List<AppInfo> allApps;
    private List<AppInfo> visibleApps;
    private PackageChangeReceiver packageReceiver;
    private ParentalControlManager controlManager;
    private final Handler clockHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private GestureDetector gestureDetector;
    private boolean isQuickPanelVisible = false;
    private boolean isSidePanelVisible = false;
    private ScreenSize currentScreenSize;
    private int currentViewMode;

    private final Runnable clockRunnable = new Runnable() {
        @Override
        public void run() {
            updateClock();
            clockHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fullscreen immersive mode
        enableImmersiveMode();

        setContentView(R.layout.activity_launcher);

        currentScreenSize = ScreenUtils.getScreenSize(this);
        controlManager = new ParentalControlManager(this);
        currentViewMode = controlManager.getViewMode();

        initViews();
        setupAppGrid();
        setupAppList();
        setupStatusBar();
        setupGestureDetector();
        setupSearch();
        setupQuickPanel();
        setupViewToggle();
        setupSidePanel();

        registerPackageReceiver();

        // Show UI immediately, load apps async
        loadAppsAsync();
    }

    private void enableImmersiveMode() {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void initViews() {
        appGrid = findViewById(R.id.app_grid);
        appList = findViewById(R.id.app_list);
        searchBox = findViewById(R.id.search_box);
        statusBar = findViewById(R.id.status_bar);
        clockText = findViewById(R.id.clock_text);
        dateText = findViewById(R.id.date_text);
        greetingText = findViewById(R.id.greeting_text);
        connectionText = findViewById(R.id.connection_text);
        batteryText = findViewById(R.id.battery_text);
        quickPanel = findViewById(R.id.quick_panel);
        quickPanelOverlay = findViewById(R.id.quick_panel_overlay);
        quickPanelContent = findViewById(R.id.quick_panel_content);
        sidePanel = findViewById(R.id.side_panel);
        btnGridView = findViewById(R.id.btn_grid_view);
        btnListView = findViewById(R.id.btn_list_view);

        ImageButton btnSettings = findViewById(R.id.btn_settings);
        ImageButton btnManage = findViewById(R.id.btn_manage);

        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            });
        }

        if (btnManage != null) {
            btnManage.setOnClickListener(v -> {
                promptPinForAction(() -> {
                    Intent intent = new Intent(this, AppManageActivity.class);
                    startActivity(intent);
                });
            });
        }
    }

    private void setupAppGrid() {
        int columns = ScreenUtils.getGridColumnCount(this);
        appGrid.setNumColumns(columns);

        appAdapter = new AppGridAdapter(this);
        appGrid.setAdapter(appAdapter);

        appGrid.setOnItemClickListener((parent, view, position, id) -> {
            AppInfo app = appAdapter.getItem(position);
            if (app != null && appAdapter.getClickListener() != null) {
                appAdapter.getClickListener().onAppClick(app);
            } else if (app != null) {
                onAppClicked(app);
            }
        });

        appGrid.setOnItemLongClickListener((parent, view, position, id) -> {
            AppInfo app = appAdapter.getItem(position);
            if (app != null) {
                return onAppLongClicked(app, view);
            }
            return false;
        });

        appAdapter.setOnAppClickListener(this::onAppClicked);
        appAdapter.setOnAppLongClickListener(this::onAppLongClicked);
    }

    private void setupAppList() {
        appListAdapter = new AppListAdapter(this);
        if (appList != null) {
            appList.setAdapter(appListAdapter);
            appListAdapter.setOnAppClickListener(this::onAppClicked);
            appListAdapter.setOnAppLongClickListener(this::onAppLongClicked);
        }
    }

    private void setupStatusBar() {
        // Always show header in new design
        if (statusBar != null) {
            statusBar.setVisibility(View.VISIBLE);
        }
    }

    private void setupGestureDetector() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null) return false;
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();

                // Vertical fling for quick panel
                if (Math.abs(diffY) > Math.abs(diffX)) {
                    if (diffY > 150 && Math.abs(velocityY) > 100) {
                        showQuickPanel();
                        return true;
                    }
                    if (diffY < -150 && Math.abs(velocityY) > 100) {
                        hideQuickPanel();
                        return true;
                    }
                }

                // Horizontal fling from left edge for TV side panel
                if (diffX > 100 && Math.abs(velocityX) > 100 && e1.getX() < 50) {
                    showSidePanel();
                    return true;
                }
                if (diffX < -100 && Math.abs(velocityX) > 100 && isSidePanelVisible) {
                    hideSidePanel();
                    return true;
                }

                return false;
            }
        });
    }

    private void setupSearch() {
        if (searchBox != null) {
            searchBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterApps(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void setupQuickPanel() {
        if (quickPanelOverlay != null) {
            quickPanelOverlay.setOnClickListener(v -> hideQuickPanel());
        }

        View btnWifi = findViewById(R.id.qp_wifi);
        View btnBluetooth = findViewById(R.id.qp_bluetooth);
        View btnNetwork = findViewById(R.id.qp_network);
        View btnHardware = findViewById(R.id.qp_hardware);
        View btnAirplane = findViewById(R.id.qp_airplane);
        View btnScreenshot = findViewById(R.id.qp_screenshot);
        View btnSettings = findViewById(R.id.qp_settings);

        if (btnWifi != null) {
            btnWifi.setOnClickListener(v -> NetworkUtils.openWifiSettings(this));
        }
        if (btnBluetooth != null) {
            btnBluetooth.setOnClickListener(v -> NetworkUtils.openBluetoothSettings(this));
        }
        if (btnNetwork != null) {
            btnNetwork.setOnClickListener(v -> NetworkUtils.openNetworkSettings(this));
        }
        if (btnHardware != null) {
            btnHardware.setOnClickListener(v -> {
                hideQuickPanel();
                startActivity(new Intent(this, HardwareInfoActivity.class));
            });
        }
        if (btnAirplane != null) {
            btnAirplane.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    // Fallback to wireless settings
                    NetworkUtils.openNetworkSettings(this);
                }
            });
        }
        if (btnScreenshot != null) {
            btnScreenshot.setOnClickListener(v -> {
                hideQuickPanel();
                // Trigger screenshot via media projection or accessibility
                // For now, show a toast-like feedback
            });
        }
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                hideQuickPanel();
                startActivity(new Intent(this, SettingsActivity.class));
            });
        }

        // Setup brightness slider
        SeekBar brightnessBar = findViewById(R.id.qp_brightness);
        if (brightnessBar != null) {
            try {
                int brightness = Settings.System.getInt(
                        getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 128);
                brightnessBar.setProgress(brightness);
            } catch (Exception e) {
                brightnessBar.setProgress(128);
            }
            brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        try {
                            Settings.System.putInt(getContentResolver(),
                                    Settings.System.SCREEN_BRIGHTNESS, progress);
                            WindowManager.LayoutParams lp = getWindow().getAttributes();
                            lp.screenBrightness = progress / 255.0f;
                            getWindow().setAttributes(lp);
                        } catch (Exception e) {
                            // May need WRITE_SETTINGS permission
                        }
                    }
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }

        // Setup volume slider
        SeekBar volumeBar = findViewById(R.id.qp_volume);
        if (volumeBar != null) {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (am != null) {
                int maxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int curVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                volumeBar.setMax(maxVol);
                volumeBar.setProgress(curVol);
            }
            volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        if (am != null) {
                            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                        }
                    }
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }
    }

    private void setupViewToggle() {
        applyViewMode();

        if (btnGridView != null) {
            btnGridView.setOnClickListener(v -> {
                currentViewMode = ParentalControlManager.VIEW_MODE_GRID;
                controlManager.setViewMode(currentViewMode);
                applyViewMode();
            });
        }
        if (btnListView != null) {
            btnListView.setOnClickListener(v -> {
                currentViewMode = ParentalControlManager.VIEW_MODE_LIST;
                controlManager.setViewMode(currentViewMode);
                applyViewMode();
            });
        }
    }

    private void applyViewMode() {
        if (currentViewMode == ParentalControlManager.VIEW_MODE_LIST) {
            if (appGrid != null) appGrid.setVisibility(View.GONE);
            if (appList != null) appList.setVisibility(View.VISIBLE);
            if (btnGridView != null) {
                btnGridView.setTextColor(getColor(R.color.text_secondary));
                btnGridView.setSelected(false);
            }
            if (btnListView != null) {
                btnListView.setTextColor(getColor(R.color.text_primary));
                btnListView.setSelected(true);
            }
        } else {
            if (appGrid != null) appGrid.setVisibility(View.VISIBLE);
            if (appList != null) appList.setVisibility(View.GONE);
            if (btnGridView != null) {
                btnGridView.setTextColor(getColor(R.color.text_primary));
                btnGridView.setSelected(true);
            }
            if (btnListView != null) {
                btnListView.setTextColor(getColor(R.color.text_secondary));
                btnListView.setSelected(false);
            }
        }
    }

    private void setupSidePanel() {
        if (sidePanel == null) return;

        // Only show side panel support on TV devices
        // It can still be opened via left edge swipe on any device

        View spWifi = findViewById(R.id.sp_wifi);
        View spBluetooth = findViewById(R.id.sp_bluetooth);
        View spAirplane = findViewById(R.id.sp_airplane);
        View spSettings = findViewById(R.id.sp_settings);

        if (spWifi != null) {
            spWifi.setOnClickListener(v -> NetworkUtils.openWifiSettings(this));
        }
        if (spBluetooth != null) {
            spBluetooth.setOnClickListener(v -> NetworkUtils.openBluetoothSettings(this));
        }
        if (spAirplane != null) {
            spAirplane.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    NetworkUtils.openNetworkSettings(this);
                }
            });
        }
        if (spSettings != null) {
            spSettings.setOnClickListener(v -> {
                hideSidePanel();
                startActivity(new Intent(this, SettingsActivity.class));
            });
        }

        // Setup side panel sliders
        SeekBar spBrightness = findViewById(R.id.sp_brightness);
        if (spBrightness != null) {
            try {
                int brightness = Settings.System.getInt(
                        getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 128);
                spBrightness.setProgress(brightness);
            } catch (Exception e) {
                spBrightness.setProgress(128);
            }
            spBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        try {
                            Settings.System.putInt(getContentResolver(),
                                    Settings.System.SCREEN_BRIGHTNESS, progress);
                            WindowManager.LayoutParams lp = getWindow().getAttributes();
                            lp.screenBrightness = progress / 255.0f;
                            getWindow().setAttributes(lp);
                        } catch (Exception e) {
                            // May need WRITE_SETTINGS permission
                        }
                    }
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }

        SeekBar spVolume = findViewById(R.id.sp_volume);
        if (spVolume != null) {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (am != null) {
                spVolume.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                spVolume.setProgress(am.getStreamVolume(AudioManager.STREAM_MUSIC));
            }
            spVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        if (am != null) {
                            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                        }
                    }
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }
    }

    void showQuickPanel() {
        if (quickPanel != null && !isQuickPanelVisible) {
            isQuickPanelVisible = true;
            quickPanel.setVisibility(View.VISIBLE);
            if (quickPanelOverlay != null) {
                quickPanelOverlay.setVisibility(View.VISIBLE);
            }
            if (quickPanelContent != null) {
                quickPanelContent.startAnimation(
                        AnimationUtils.loadAnimation(this, R.anim.slide_down));
            }
            updateQuickPanelInfo();
        }
    }

    void hideQuickPanel() {
        if (quickPanel != null && isQuickPanelVisible) {
            isQuickPanelVisible = false;
            if (quickPanelContent != null) {
                quickPanelContent.startAnimation(
                        AnimationUtils.loadAnimation(this, R.anim.slide_up));
            }
            quickPanel.setVisibility(View.GONE);
            if (quickPanelOverlay != null) {
                quickPanelOverlay.setVisibility(View.GONE);
            }
        }
    }

    private void showSidePanel() {
        if (sidePanel != null && !isSidePanelVisible) {
            isSidePanelVisible = true;
            sidePanel.setVisibility(View.VISIBLE);
            updateSidePanelInfo();
        }
    }

    private void hideSidePanel() {
        if (sidePanel != null && isSidePanelVisible) {
            isSidePanelVisible = false;
            sidePanel.setVisibility(View.GONE);
        }
    }

    private void updateQuickPanelInfo() {
        TextView wifiStatus = findViewById(R.id.qp_wifi_status);
        TextView btStatus = findViewById(R.id.qp_bt_status);
        TextView netStatus = findViewById(R.id.qp_net_status);
        TextView batteryDisplay = findViewById(R.id.qp_battery_text);

        if (wifiStatus != null) {
            boolean wifiEnabled = NetworkUtils.isWifiEnabled(this);
            String ssid = NetworkUtils.getCurrentWifiSsid(this);
            wifiStatus.setText(wifiEnabled ? (ssid != null ? ssid : getString(R.string.wifi_on)) : getString(R.string.wifi_off));
        }

        if (btStatus != null) {
            btStatus.setText(NetworkUtils.isBluetoothEnabled() ? getString(R.string.bt_on) : getString(R.string.bt_off));
        }

        if (netStatus != null) {
            netStatus.setText(NetworkUtils.getConnectionType(this));
        }

        if (batteryDisplay != null) {
            int level = DeviceUtils.getBatteryLevel(this);
            batteryDisplay.setText(level >= 0 ? "🔋 " + level + "%" : "🔋 --");
        }
    }

    private void updateSidePanelInfo() {
        TextView spWifiStatus = findViewById(R.id.sp_wifi_status);
        TextView spBtStatus = findViewById(R.id.sp_bt_status);

        if (spWifiStatus != null) {
            boolean wifiEnabled = NetworkUtils.isWifiEnabled(this);
            String ssid = NetworkUtils.getCurrentWifiSsid(this);
            spWifiStatus.setText(wifiEnabled ? (ssid != null ? ssid : getString(R.string.wifi_on)) : getString(R.string.wifi_off));
        }

        if (spBtStatus != null) {
            spBtStatus.setText(NetworkUtils.isBluetoothEnabled() ? getString(R.string.bt_on) : getString(R.string.bt_off));
        }
    }

    /**
     * Load apps asynchronously to keep UI responsive.
     */
    void loadAppsAsync() {
        executor.execute(() -> {
            List<AppInfo> apps = AppUtils.getInstalledApps(this);
            List<AppInfo> filtered = filterByParentalControls(apps);
            new Handler(Looper.getMainLooper()).post(() -> {
                allApps = apps;
                visibleApps = filtered;
                appAdapter.updateApps(filtered);
                if (appListAdapter != null) {
                    appListAdapter.updateApps(filtered);
                }
            });
        });
    }

    /**
     * Synchronous load for compatibility with existing test infrastructure.
     */
    void loadApps() {
        allApps = AppUtils.getInstalledApps(this);
        visibleApps = filterByParentalControls(allApps);
        appAdapter.updateApps(visibleApps);
        if (appListAdapter != null) {
            appListAdapter.updateApps(visibleApps);
        }
    }

    private List<AppInfo> filterByParentalControls(List<AppInfo> apps) {
        if (controlManager == null) return apps;
        Set<String> hidden = controlManager.getHiddenApps();
        if (hidden.isEmpty()) return apps;

        List<AppInfo> visible = new ArrayList<>();
        for (AppInfo app : apps) {
            if (!hidden.contains(app.getPackageName())) {
                visible.add(app);
            }
        }
        return visible;
    }

    private void filterApps(String query) {
        if (visibleApps != null) {
            List<AppInfo> filtered = AppUtils.filterApps(visibleApps, query);
            appAdapter.updateApps(filtered);
            if (appListAdapter != null) {
                appListAdapter.updateApps(filtered);
            }
        }
    }

    private void onAppClicked(AppInfo appInfo) {
        AppUtils.launchApp(this, appInfo.getComponentName());
    }

    private boolean onAppLongClicked(AppInfo appInfo, View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenu().add(0, 1, 0, R.string.app_info);
        if (!appInfo.isSystemApp()) {
            popup.getMenu().add(0, 2, 1, R.string.uninstall);
        }

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 1:
                    AppUtils.openAppDetails(this, appInfo.getPackageName());
                    return true;
                case 2:
                    AppUtils.requestUninstall(this, appInfo.getPackageName());
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
        return true;
    }

    private void updateClock() {
        if (clockText != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            clockText.setText(sdf.format(new Date()));
        }
        if (dateText != null) {
            SimpleDateFormat dateFmt = new SimpleDateFormat("EEEE, MMM d", Locale.getDefault());
            dateText.setText(dateFmt.format(new Date()));
        }
        if (greetingText != null) {
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            if (hour < 12) {
                greetingText.setText(R.string.greeting_morning);
            } else if (hour < 18) {
                greetingText.setText(R.string.greeting_afternoon);
            } else {
                greetingText.setText(R.string.greeting_evening);
            }
        }
        if (connectionText != null) {
            connectionText.setText(NetworkUtils.getConnectionType(this));
        }
        if (batteryText != null) {
            int level = DeviceUtils.getBatteryLevel(this);
            batteryText.setText(level >= 0 ? level + "%" : "--");
        }
    }

    /**
     * Prompt user for PIN before performing a protected action.
     */
    private void promptPinForAction(Runnable action) {
        final EditText pinInput = new EditText(this);
        pinInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER
                | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);
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

    private void registerPackageReceiver() {
        packageReceiver = new PackageChangeReceiver();
        packageReceiver.setOnPackageChangeListener(this::loadAppsAsync);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        registerReceiver(packageReceiver, filter);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (gestureDetector != null) {
            gestureDetector.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // TV remote: DPAD_LEFT at left edge opens side panel
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (!isSidePanelVisible) {
                    showSidePanel();
                    return true;
                }
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT && isSidePanelVisible) {
                hideSidePanel();
                return true;
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
                if (isQuickPanelVisible) {
                    hideQuickPanel();
                } else {
                    showQuickPanel();
                }
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableImmersiveMode();
        loadAppsAsync();
        clockHandler.post(clockRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        clockHandler.removeCallbacks(clockRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (packageReceiver != null) {
            try {
                unregisterReceiver(packageReceiver);
            } catch (IllegalArgumentException e) {
                // Receiver not registered
            }
        }
        clockHandler.removeCallbacksAndMessages(null);
        executor.shutdown();
    }

    @Override
    public void onBackPressed() {
        if (isSidePanelVisible) {
            hideSidePanel();
        } else if (isQuickPanelVisible) {
            hideQuickPanel();
        }
        // Don't call super - we are the home screen
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            enableImmersiveMode();
        }
    }

    // Visible for testing
    boolean isQuickPanelVisible() {
        return isQuickPanelVisible;
    }

    ScreenSize getCurrentScreenSize() {
        return currentScreenSize;
    }

    AppGridAdapter getAppAdapter() {
        return appAdapter;
    }
}
