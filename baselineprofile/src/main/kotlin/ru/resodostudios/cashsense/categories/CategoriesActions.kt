package ru.resodostudios.cashsense.categories

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import ru.resodostudios.cashsense.waitForObjectOnTopAppBar

fun MacrobenchmarkScope.goToCategoriesScreen() {
    device.findObject(By.text("Categories")).click()
    device.waitForIdle()
    waitForObjectOnTopAppBar(By.text("Categories"))

    // Wait until content is loaded by checking if categories are loaded
    device.wait(Until.gone(By.res("loadingCircle")), 5_000)
}