package com.kidsafe.launcher.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidsafe.launcher.R;
import com.kidsafe.launcher.models.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * BaseAdapter for displaying apps in the management/uninstall list.
 */
public class AppManageAdapter extends BaseAdapter {

    private final Context context;
    private List<AppInfo> apps;
    private OnUninstallClickListener uninstallListener;
    private OnInfoClickListener infoListener;

    public interface OnUninstallClickListener {
        void onUninstallClick(AppInfo appInfo);
    }

    public interface OnInfoClickListener {
        void onInfoClick(AppInfo appInfo);
    }

    public AppManageAdapter(Context context) {
        this.context = context;
        this.apps = new ArrayList<>();
    }

    public AppManageAdapter(Context context, List<AppInfo> apps) {
        this.context = context;
        this.apps = apps != null ? new ArrayList<>(apps) : new ArrayList<>();
    }

    public void setOnUninstallClickListener(OnUninstallClickListener listener) {
        this.uninstallListener = listener;
    }

    public void setOnInfoClickListener(OnInfoClickListener listener) {
        this.infoListener = listener;
    }

    public void updateApps(List<AppInfo> newApps) {
        this.apps.clear();
        if (newApps != null) {
            this.apps.addAll(newApps);
        }
        notifyDataSetChanged();
    }

    public List<AppInfo> getApps() {
        return new ArrayList<>(apps);
    }

    @Override
    public int getCount() {
        return apps.size();
    }

    @Override
    public AppInfo getItem(int position) {
        return apps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_app_manage, parent, false);
            holder = new ViewHolder();
            holder.iconView = convertView.findViewById(R.id.manage_app_icon);
            holder.labelView = convertView.findViewById(R.id.manage_app_label);
            holder.packageView = convertView.findViewById(R.id.manage_app_package);
            holder.uninstallButton = convertView.findViewById(R.id.btn_uninstall);
            holder.infoButton = convertView.findViewById(R.id.btn_info);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AppInfo app = apps.get(position);
        holder.labelView.setText(app.getLabel());
        holder.packageView.setText(app.getPackageName());
        if (app.getIcon() != null) {
            holder.iconView.setImageDrawable(app.getIcon());
        }

        holder.uninstallButton.setEnabled(!app.isSystemApp());
        holder.uninstallButton.setAlpha(app.isSystemApp() ? 0.3f : 1.0f);

        holder.uninstallButton.setOnClickListener(v -> {
            if (uninstallListener != null && !app.isSystemApp()) {
                uninstallListener.onUninstallClick(app);
            }
        });

        holder.infoButton.setOnClickListener(v -> {
            if (infoListener != null) {
                infoListener.onInfoClick(app);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        ImageView iconView;
        TextView labelView;
        TextView packageView;
        ImageButton uninstallButton;
        ImageButton infoButton;
    }
}
