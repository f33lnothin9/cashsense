package ru.resodostudios.cashsense.subscriptions

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until

fun MacrobenchmarkScope.goToSubscriptionsScreen() {
    device.findObject(By.text("Subscriptions")).click()
    device.waitForIdle()

    // Wait until content is loaded by checking if subscriptions are loaded
    device.wait(Until.gone(By.res("loadingCircle")), 5_000)
}