package ru.resodostudios.cashsense.core.ui

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDateTime

fun Instant.getZonedDateTime() =
    toLocalDateTime(TimeZone.currentSystemDefault()).toJavaLocalDateTime()

fun getCurrentZonedDateTime() =
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toJavaLocalDateTime()

fun LocalDateTime.isInCurrentMonthAndYear(): Boolean {
    val currentDate = getCurrentZonedDateTime()
    return this.year == currentDate.year && this.month == currentDate.month
}