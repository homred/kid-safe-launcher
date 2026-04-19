package com.kidsafe.launcher.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.kidsafe.launcher.R;
import com.kidsafe.launcher.models.AppInfo;
import com.kidsafe.launcher.utils.AppUtils;
import com.kidsafe.launcher.utils.ParentalControlManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity for parents to manage which apps are visible to children.
 * Protected by PIN verification before this activity can be opened.
 */
public class ParentalControlActivity extends Activity {

    private ListView appListView;
    private ParentalControlManager controlManager;
    private List<AppInfo> allApps;
    private AppVisibilityAdapter adapter;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parental_control);

        controlManager = new ParentalControlManager(this);
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
                getActionBar().setTitle(R.string.visible_apps);
            }
        }
    }

    private void setupViews() {
        appListView = findViewById(R.id.app_visibility_list);
        adapter = new AppVisibilityAdapter();
        appListView.setAdapter(adapter);
    }

    private void loadApps() {
        executor.execute(() -> {
            List<AppInfo> apps = AppUtils.getInstalledApps(this);
            mainHandler.post(() -> {
                allApps = apps;
                adapter.updateApps(apps);
            });
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    /**
     * Adapter for managing app visibility checkboxes.
     */
    private class AppVisibilityAdapter extends BaseAdapter {
        private final List<AppInfo> apps = new ArrayList<>();

        void updateApps(List<AppInfo> newApps) {
            apps.clear();
            if (newApps != null) {
                apps.addAll(newApps);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() { return apps.size(); }

        @Override
        public AppInfo getItem(int pos) { return apps.get(pos); }

        @Override
        public long getItemId(int pos) { return pos; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(ParentalControlActivity.this);
                convertView = inflater.inflate(R.layout.item_app_visibility, parent, false);
                holder = new ViewHolder();
                holder.icon = convertView.findViewById(R.id.app_icon);
                holder.label = convertView.findViewById(R.id.app_label);
                holder.pkg = convertView.findViewById(R.id.app_package);
                holder.toggle = convertView.findViewById(R.id.app_visible_toggle);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            AppInfo app = apps.get(position);
            holder.label.setText(app.getLabel());
            holder.pkg.setText(app.getPackageName());
            if (app.getIcon() != null) {
                holder.icon.setImageDrawable(app.getIcon());
            }

            boolean isHidden = controlManager.isAppHidden(app.getPackageName());
            holder.toggle.setChecked(!isHidden);
            holder.toggle.setOnCheckedChangeListener(null);
            holder.toggle.setOnCheckedChangeListener((btn, isChecked) -> {
                controlManager.toggleAppVisibility(app.getPackageName());
            });

            convertView.setOnClickListener(v -> {
                holder.toggle.setChecked(!holder.toggle.isChecked());
            });

            return convertView;
        }

        class ViewHolder {
            ImageView icon;
            TextView label;
            TextView pkg;
            CheckBox toggle;
        }
    }
}
