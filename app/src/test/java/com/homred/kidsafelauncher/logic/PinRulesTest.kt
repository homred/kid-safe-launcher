package com.homred.kidsafelauncher.logic

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PinRulesTest {
    @Test
    fun validPinRange() {
        assertTrue(PinRules.isValid("1234"))
        assertTrue(PinRules.isValid("123456"))
        assertFalse(PinRules.isValid("123"))
        assertFalse(PinRules.isValid("1234567"))
        assertFalse(PinRules.isValid("12ab"))
    }

    @Test
    fun verifyPin() {
        assertTrue(PinRules.verify("1234", "1234"))
        assertFalse(PinRules.verify("1234", "1235"))
        assertFalse(PinRules.verify("1234", "123"))
    }
}
