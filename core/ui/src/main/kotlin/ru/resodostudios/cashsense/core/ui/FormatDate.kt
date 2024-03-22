package ru.resodostudios.cashsense.core.ui

import androidx.compose.runtime.Composable
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaZoneId
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun Instant.formatDate(
    withTime: Boolean = false,
): String {

    val zoneId = LocalTimeZone.current.toJavaZoneId()

    return if (withTime) {
        DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.SHORT)
            .withLocale(Locale.getDefault())
            .withZone(zoneId)
            .format(toJavaInstant())
    } else {
        DateTimeFormatter
            .ofLocalizedDate(FormatStyle.MEDIUM)
            .withLocale(Locale.getDefault())
            .withZone(zoneId)
            .format(toJavaInstant())
    }
}

fun Instant.getZonedDateTime() = toLocalDateTime(TimeZone.currentSystemDefault())

fun getCurrentZonedDateTime() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())