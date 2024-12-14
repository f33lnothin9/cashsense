package ru.resodostudios.cashsense.settings

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import ru.resodostudios.cashsense.waitForObjectOnTopAppBar

fun MacrobenchmarkScope.goToSettingsScreen() {
    device.findObject(By.text("Settings")).click()
    waitForObjectOnTopAppBar(By.text("Settings"))

    // Wait until content is loaded by checking if settings are loaded
    device.wait(Until.gone(By.res("loadingCircle")), 1_000)
}