package com.homred.kidsafelauncher.logic

import com.homred.kidsafelauncher.model.AppEntry

object AppFilter {
    fun filterByName(apps: List<AppEntry>, query: String): List<AppEntry> {
        if (query.isBlank()) return apps
        val normalized = query.trim().lowercase()
        return apps.filter { it.name.lowercase().contains(normalized) }
    }
}
