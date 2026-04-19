package com.homred.kidsafelauncher.data

import android.content.Context

class SettingsStore(context: Context) {
    private val prefs = context.getSharedPreferences("kid_safe_launcher", Context.MODE_PRIVATE)

    fun getPin(): String? = prefs.getString(KEY_PIN, null)

    fun savePin(pin: String) {
        prefs.edit().putString(KEY_PIN, pin).apply()
    }

    fun getWhitelist(): Set<String> = prefs.getStringSet(KEY_WHITELIST, emptySet()) ?: emptySet()

    fun saveWhitelist(whitelist: Set<String>) {
        prefs.edit().putStringSet(KEY_WHITELIST, whitelist).apply()
    }

    fun shouldShowGuide(): Boolean = prefs.getBoolean(KEY_FIRST_GUIDE, true)

    fun markGuideShown() {
        prefs.edit().putBoolean(KEY_FIRST_GUIDE, false).apply()
    }

    companion object {
        private const val KEY_PIN = "pin"
        private const val KEY_WHITELIST = "whitelist"
        private const val KEY_FIRST_GUIDE = "first_guide"
    }
}
