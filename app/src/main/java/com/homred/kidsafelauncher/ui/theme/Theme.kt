package com.homred.kidsafelauncher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = darkColorScheme()

@Composable
fun KidSafeLauncherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        content = content
    )
}
