package com.homred.kidsafelauncher.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast

fun Context.safeStartActivity(intent: Intent, unsupportedMessage: String): Boolean {
    return try {
        if (this !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        true
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(this, unsupportedMessage, Toast.LENGTH_SHORT).show()
        false
    } catch (_: SecurityException) {
        Toast.makeText(this, unsupportedMessage, Toast.LENGTH_SHORT).show()
        false
    }
}
