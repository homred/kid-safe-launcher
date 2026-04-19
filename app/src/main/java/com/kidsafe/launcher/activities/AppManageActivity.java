package com.kidsafe.launcher.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toolbar;

import com.kidsafe.launcher.R;
import com.kidsafe.launcher.adapters.AppManageAdapter;
import com.kidsafe.launcher.models.AppInfo;
import com.kidsafe.launcher.utils.AppUtils;

import java.util.List;

/**
 * Activity for managing and uninstalling applications.
 */
public class AppManageActivity extends Activity {

    private ListView appList;
    private AppManageAdapter adapter;
    private EditText searchBox;
    private List<AppInfo> allApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manage);

        setupToolbar();
        setupViews();
        loadApps();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setActionBar(toolbar);
            if (getActionBar() != null) {
                getActionBar().setDisplayHomeAsUpEnabled(true);
                getActionBar().setTitle(R.string.manage_apps);
            }
        }
    }

    private void setupViews() {
        appList = findViewById(R.id.manage_app_list);
        searchBox = findViewById(R.id.manage_search_box);

        adapter = new AppManageAdapter(this);
        adapter.setOnUninstallClickListener(this::confirmUninstall);
        adapter.setOnInfoClickListener(app -> AppUtils.openAppDetails(this, app.getPackageName()));
        appList.setAdapter(adapter);

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

    void loadApps() {
        allApps = AppUtils.getInstalledApps(this);
        adapter.updateApps(allApps);
    }

    private void filterApps(String query) {
        if (allApps != null) {
            List<AppInfo> filtered = AppUtils.filterApps(allApps, query);
            adapter.updateApps(filtered);
        }
    }

    private void confirmUninstall(AppInfo app) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_uninstall)
                .setMessage(getString(R.string.confirm_uninstall_msg, app.getLabel()))
                .setPositiveButton(R.string.uninstall, (d, w) -> {
                    AppUtils.requestUninstall(this, app.getPackageName());
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadApps();
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
