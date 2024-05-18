package ru.resodostudios.cashsense.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import org.junit.Rule
import org.junit.Test
import ru.resodostudios.cashsense.PACKAGE_NAME
import ru.resodostudios.cashsense.home.homeWaitForContent
import ru.resodostudios.cashsense.startActivityAndAllowNotifications

class HomeBaselineProfile {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() = baselineProfileRule.collect(PACKAGE_NAME) {
        startActivityAndAllowNotifications()
        homeWaitForContent()
    }
}