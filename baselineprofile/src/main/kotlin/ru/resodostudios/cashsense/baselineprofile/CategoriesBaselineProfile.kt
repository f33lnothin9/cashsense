package ru.resodostudios.cashsense.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import org.junit.Rule
import org.junit.Test
import ru.resodostudios.cashsense.PACKAGE_NAME
import ru.resodostudios.cashsense.categories.goToCategoriesScreen

class CategoriesBaselineProfile {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() = baselineProfileRule.collect(PACKAGE_NAME) {
        startActivityAndWait()

        goToCategoriesScreen()
    }
}