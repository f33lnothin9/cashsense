package ru.resodostudios.cashsense

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until

/**
 * Waits for and returns the `csTopAppBar`
 */
fun MacrobenchmarkScope.getTopAppBar(): UiObject2 {
    device.wait(Until.hasObject(By.res("csTopAppBar")), 1_000)
    return device.findObject(By.res("csTopAppBar"))
}

/**
 * Waits for an object on the top app bar, passed in as [selector].
 */
fun MacrobenchmarkScope.waitForObjectOnTopAppBar(selector: BySelector, timeout: Long = 1_000) {
    getTopAppBar().wait(Until.hasObject(selector), timeout)
}