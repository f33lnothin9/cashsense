package ru.resodostudios.cashsense.home

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until

fun MacrobenchmarkScope.homeWaitForContent() {
    device.wait(Until.gone(By.res("loadingCircle")), 1_000)
}