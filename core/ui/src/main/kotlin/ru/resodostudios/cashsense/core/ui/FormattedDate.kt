package ru.resodostudios.cashsense.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun FormattedDate(date: Instant): String {
    var zoneId by remember { mutableStateOf(ZoneId.systemDefault()) }

    val context = LocalContext.current

    DisposableEffect(context) {
        val receiver = TimeZoneBroadcastReceiver(
            onTimeZoneChanged = { zoneId = ZoneId.systemDefault() },
        )
        receiver.register(context)
        onDispose {
            receiver.unregister(context)
        }
    }

    return DateTimeFormatter
        .ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale.getDefault())
        .withZone(zoneId)
        .format(date.toJavaInstant())
}