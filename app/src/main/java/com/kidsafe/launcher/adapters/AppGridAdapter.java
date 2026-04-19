package com.kidsafe.launcher.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kidsafe.launcher.R;
import com.kidsafe.launcher.models.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for displaying installed apps in a grid.
 */
public class AppGridAdapter extends RecyclerView.Adapter<AppGridAdapter.AppViewHolder> {

    private List<AppInfo> apps;
    private OnAppClickListener clickListener;
    private OnAppLongClickListener longClickListener;

    public interface OnAppClickListener {
        void onAppClick(AppInfo appInfo);
    }

    public interface OnAppLongClickListener {
        boolean onAppLongClick(AppInfo appInfo, View view);
    }

    public AppGridAdapter() {
        this.apps = new ArrayList<>();
    }

    public AppGridAdapter(List<AppInfo> apps) {
        this.apps = apps != null ? new ArrayList<>(apps) : new ArrayList<>();
    }

    public void setOnAppClickListener(OnAppClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnAppLongClickListener(OnAppLongClickListener listener) {
        this.longClickListener = listener;
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
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppInfo app = apps.get(position);
        holder.bind(app);
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    class AppViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iconView;
        private final TextView labelView;

        AppViewHolder(@NonNull View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.app_icon);
            labelView = itemView.findViewById(R.id.app_label);
        }

        void bind(AppInfo app) {
            labelView.setText(app.getLabel());
            if (app.getIcon() != null) {
                iconView.setImageDrawable(app.getIcon());
            }

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onAppClick(app);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    return longClickListener.onAppLongClick(app, v);
                }
                return false;
            });
        }
    }
}
