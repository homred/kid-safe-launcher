package com.kidsafe.launcher.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsafe.launcher.R;
import com.kidsafe.launcher.models.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for displaying apps in the management/uninstall list.
 */
public class AppManageAdapter extends RecyclerView.Adapter<AppManageAdapter.ManageViewHolder> {

    private List<AppInfo> apps;
    private OnUninstallClickListener uninstallListener;
    private OnInfoClickListener infoListener;

    public interface OnUninstallClickListener {
        void onUninstallClick(AppInfo appInfo);
    }

    public interface OnInfoClickListener {
        void onInfoClick(AppInfo appInfo);
    }

    public AppManageAdapter() {
        this.apps = new ArrayList<>();
    }

    public AppManageAdapter(List<AppInfo> apps) {
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

    @NonNull
    @Override
    public ManageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app_manage, parent, false);
        return new ManageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageViewHolder holder, int position) {
        AppInfo app = apps.get(position);
        holder.bind(app);
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    class ManageViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iconView;
        private final TextView labelView;
        private final TextView packageView;
        private final ImageButton uninstallButton;
        private final ImageButton infoButton;

        ManageViewHolder(@NonNull View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.manage_app_icon);
            labelView = itemView.findViewById(R.id.manage_app_label);
            packageView = itemView.findViewById(R.id.manage_app_package);
            uninstallButton = itemView.findViewById(R.id.btn_uninstall);
            infoButton = itemView.findViewById(R.id.btn_info);
        }

        void bind(AppInfo app) {
            labelView.setText(app.getLabel());
            packageView.setText(app.getPackageName());
            if (app.getIcon() != null) {
                iconView.setImageDrawable(app.getIcon());
            }

            // Disable uninstall for system apps
            uninstallButton.setEnabled(!app.isSystemApp());
            uninstallButton.setAlpha(app.isSystemApp() ? 0.3f : 1.0f);

            uninstallButton.setOnClickListener(v -> {
                if (uninstallListener != null && !app.isSystemApp()) {
                    uninstallListener.onUninstallClick(app);
                }
            });

            infoButton.setOnClickListener(v -> {
                if (infoListener != null) {
                    infoListener.onInfoClick(app);
                }
            });
        }
    }
}
