package com.homred.kidsafelauncher.model

import android.graphics.drawable.Drawable

data class AppEntry(
    val packageName: String,
    val name: String,
    val icon: Drawable?,
    val canUninstall: Boolean,
)
