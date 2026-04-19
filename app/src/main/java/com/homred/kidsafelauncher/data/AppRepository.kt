package com.homred.kidsafelauncher.data

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import com.homred.kidsafelauncher.model.AppEntry

class AppRepository(private val context: Context) {
    fun getLaunchableApps(): List<AppEntry> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return pm.queryIntentActivities(intent, 0)
            .map { resolveInfo ->
                val appInfo = resolveInfo.activityInfo.applicationInfo
                val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                AppEntry(
                    packageName = resolveInfo.activityInfo.packageName,
                    name = resolveInfo.loadLabel(pm).toString(),
                    icon = resolveInfo.loadIcon(pm),
                    canUninstall = !isSystem,
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.name.lowercase() }
    }
}
