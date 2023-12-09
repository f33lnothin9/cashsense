package ru.resodostudios.cashsense.feature.transactions

import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import ru.resodostudios.cashsense.core.model.data.Currency
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.ui.DefaultDropdownMenu
import ru.resodostudios.cashsense.core.ui.AmountWithCurrencyText
import ru.resodostudios.cashsense.core.ui.TimeZoneBroadcastReceiver
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

fun LazyGridScope.transactions(
    transactions: List<Transaction>,
    currency: Currency,
    onDelete: (Transaction) -> Unit
) {
    items(transactions) { transaction ->
        ListItem(
            headlineContent = { AmountWithCurrencyText(sum = transaction.amount, currency = currency) },
            trailingContent = {
                DefaultDropdownMenu(
                    onEdit = { /*TODO*/ },
                    onDelete = { onDelete(transaction) }
                )
            },
            supportingContent = { Text(text = dateFormatted(transaction.date)) }
        )
    }
}

@Composable
fun dateFormatted(date: Instant): String {
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
        .ofLocalizedDateTime(FormatStyle.MEDIUM)
        .withLocale(Locale.getDefault())
        .withZone(zoneId)
        .format(date.toJavaInstant())
}