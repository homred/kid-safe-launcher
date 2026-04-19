package com.homred.kidsafelauncher.logic

object PinRules {
    private val pinRegex = Regex("^[0-9]{4,6}$")

    fun isValid(pin: String): Boolean = pinRegex.matches(pin)

    fun verify(savedPin: String, enteredPin: String): Boolean =
        isValid(enteredPin) && savedPin == enteredPin
}
