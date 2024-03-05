package ru.resodostudios.cashsense.core.ui

import androidx.compose.runtime.Composable
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun dateFormatted(
    date: Instant,
    withTime: Boolean = false,
): String {

    val zoneId = LocalTimeZone.current.toJavaZoneId()

    return if (withTime) {
        DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.SHORT)
            .withLocale(Locale.getDefault())
            .withZone(zoneId)
            .format(date.toJavaInstant())
    } else {
        DateTimeFormatter
            .ofLocalizedDate(FormatStyle.MEDIUM)
            .withLocale(Locale.getDefault())
            .withZone(zoneId)
            .format(date.toJavaInstant())
    }
}