package ru.resodostudios.cashsense.home

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import androidx.test.uiautomator.untilHasChildren
import ru.resodostudios.cashsense.waitAndFindObject

fun MacrobenchmarkScope.homeWaitForContent() {
    device.wait(Until.gone(By.res("loadingCircle")), 5_000)
    val obj = device.waitAndFindObject(By.res("home:wallets"), 10_000)
    obj.wait(untilHasChildren(), 10_000)
}