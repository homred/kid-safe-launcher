package com.homred.kidsafelauncher.logic

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RestrictionPolicyTest {
    @Test
    fun whitelistEmptyMeansNoRestriction() {
        assertFalse(RestrictionPolicy.isRestricted("pkg", emptySet()))
    }

    @Test
    fun nonWhitelistedAppIsRestricted() {
        val whitelist = setOf("allowed.pkg")
        assertFalse(RestrictionPolicy.isRestricted("allowed.pkg", whitelist))
        assertTrue(RestrictionPolicy.isRestricted("blocked.pkg", whitelist))
    }
}
