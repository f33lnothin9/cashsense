package ru.resodostudios.cashsense.core.model.data

import ru.resodostudios.cashsense.core.model.data.RepeatingIntervalType.NONE

enum class RepeatingIntervalType(val period: Long) {
    NONE(0L),
    DAILY(86400000L),
    WEEKLY(7L * 86400000L),
    MONTHLY(30L * 86400000L),
    YEARLY(365L * 86400000L),
}

fun getRepeatingIntervalType(repeatingInterval: Long?): RepeatingIntervalType =
    RepeatingIntervalType.entries.firstOrNull { it.period == repeatingInterval } ?: NONE