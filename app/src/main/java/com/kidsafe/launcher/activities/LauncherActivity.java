package com.kidsafe.launcher.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.kidsafe.launcher.R;
import com.kidsafe.launcher.adapters.AppGridAdapter;
import com.kidsafe.launcher.models.AppInfo;
import com.kidsafe.launcher.models.ScreenSize;
import com.kidsafe.launcher.receivers.PackageChangeReceiver;
import com.kidsafe.launcher.utils.AppUtils;
import com.kidsafe.launcher.utils.NetworkUtils;
import com.kidsafe.launcher.utils.ScreenUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Main launcher activity that replaces the device home screen.
 * Supports adaptive layout for watch, phone, tablet, and TV screens.
 */
public class LauncherActivity extends Activity {

    private GridView appGrid;
    private AppGridAdapter appAdapter;
    private EditText searchBox;
    private View statusBar;
    private TextView clockText;
    private TextView connectionText;
    private View quickPanel;
    private View quickPanelOverlay;
    private LinearLayout quickPanelContent;

    private List<AppInfo> allApps;
    private PackageChangeReceiver packageReceiver;
    private final Handler clockHandler = new Handler(Looper.getMainLooper());
    private GestureDetector gestureDetector;
    private boolean isQuickPanelVisible = false;
    private ScreenSize currentScreenSize;

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
        setContentView(R.layout.activity_launcher);

        currentScreenSize = ScreenUtils.getScreenSize(this);

        initViews();
        setupAppGrid();
        setupStatusBar();
        setupGestureDetector();
        setupSearch();
        setupQuickPanel();

        registerPackageReceiver();
        loadApps();
    }

    private void initViews() {
        appGrid = findViewById(R.id.app_grid);
        searchBox = findViewById(R.id.search_box);
        statusBar = findViewById(R.id.status_bar);
        clockText = findViewById(R.id.clock_text);
        connectionText = findViewById(R.id.connection_text);
        quickPanel = findViewById(R.id.quick_panel);
        quickPanelOverlay = findViewById(R.id.quick_panel_overlay);
        quickPanelContent = findViewById(R.id.quick_panel_content);

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
                Intent intent = new Intent(this, AppManageActivity.class);
                startActivity(intent);
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

    private void setupStatusBar() {
        if (statusBar != null) {
            boolean showStatusBar = ScreenUtils.shouldShowStatusBar(this);
            statusBar.setVisibility(showStatusBar ? View.VISIBLE : View.GONE);
        }
    }

    private void setupGestureDetector() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null) return false;
                float diffY = e2.getY() - e1.getY();
                if (diffY > 150 && Math.abs(velocityY) > 100) {
                    showQuickPanel();
                    return true;
                }
                if (diffY < -150 && Math.abs(velocityY) > 100) {
                    hideQuickPanel();
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

    private void updateQuickPanelInfo() {
        TextView wifiStatus = findViewById(R.id.qp_wifi_status);
        TextView btStatus = findViewById(R.id.qp_bt_status);
        TextView netStatus = findViewById(R.id.qp_net_status);

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
    }

    void loadApps() {
        allApps = AppUtils.getInstalledApps(this);
        appAdapter.updateApps(allApps);
    }

    private void filterApps(String query) {
        if (allApps != null) {
            List<AppInfo> filtered = AppUtils.filterApps(allApps, query);
            appAdapter.updateApps(filtered);
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
        if (connectionText != null) {
            connectionText.setText(NetworkUtils.getConnectionType(this));
        }
    }

    private void registerPackageReceiver() {
        packageReceiver = new PackageChangeReceiver();
        packageReceiver.setOnPackageChangeListener(this::loadApps);

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
    protected void onResume() {
        super.onResume();
        loadApps();
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
    }

    @Override
    public void onBackPressed() {
        if (isQuickPanelVisible) {
            hideQuickPanel();
        }
        // Don't call super - we are the home screen
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
