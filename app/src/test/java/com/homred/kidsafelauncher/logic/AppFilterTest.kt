package com.homred.kidsafelauncher.logic

import com.homred.kidsafelauncher.model.AppEntry
import org.junit.Assert.assertEquals
import org.junit.Test

class AppFilterTest {
    @Test
    fun filterByNameCaseInsensitive() {
        val apps = listOf(
            AppEntry("a", "Calculator", null, true),
            AppEntry("b", "Camera", null, true),
            AppEntry("c", "Gallery", null, true),
        )

        val result = AppFilter.filterByName(apps, "ca")

        assertEquals(listOf("Calculator", "Camera"), result.map { it.name })
    }
}
