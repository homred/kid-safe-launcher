package com.homred.kidsafelauncher.logic

object RestrictionPolicy {
    // V1 uses whitelist mode: when whitelist has entries, non-whitelisted apps are restricted.
    fun isRestricted(packageName: String, whitelist: Set<String>): Boolean {
        if (whitelist.isEmpty()) return false
        return packageName !in whitelist
    }
}
