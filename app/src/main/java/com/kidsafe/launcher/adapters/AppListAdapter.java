package com.kidsafe.launcher.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidsafe.launcher.R;
import com.kidsafe.launcher.models.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * BaseAdapter for displaying installed apps in a ListView (list mode).
 */
public class AppListAdapter extends BaseAdapter {

    private final Context context;
    private List<AppInfo> apps;
    private OnAppClickListener clickListener;
    private OnAppLongClickListener longClickListener;

    public interface OnAppClickListener {
        void onAppClick(AppInfo appInfo);
    }

    public interface OnAppLongClickListener {
        boolean onAppLongClick(AppInfo appInfo, View view);
    }

    public AppListAdapter(Context context) {
        this.context = context;
        this.apps = new ArrayList<>();
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
            convertView = inflater.inflate(R.layout.item_app_list, parent, false);
            holder = new ViewHolder();
            holder.iconView = convertView.findViewById(R.id.app_icon);
            holder.labelView = convertView.findViewById(R.id.app_label);
            holder.packageView = convertView.findViewById(R.id.app_package);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AppInfo app = apps.get(position);
        holder.labelView.setText(app.getLabel());
        if (holder.packageView != null) {
            holder.packageView.setText(app.getPackageName());
        }
        if (app.getIcon() != null) {
            holder.iconView.setImageDrawable(app.getIcon());
        }

        final View itemView = convertView;
        convertView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onAppClick(app);
            }
        });
        convertView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                return longClickListener.onAppLongClick(app, itemView);
            }
            return false;
        });

        return convertView;
    }

    public OnAppClickListener getClickListener() {
        return clickListener;
    }

    private static class ViewHolder {
        ImageView iconView;
        TextView labelView;
        TextView packageView;
    }
}
