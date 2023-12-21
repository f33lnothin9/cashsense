package ru.resodostudios.cashsense.feature.transaction

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import ru.resodostudios.cashsense.core.model.data.Currency
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.ui.AmountWithCurrencyText
import ru.resodostudios.cashsense.core.ui.EditAndDeleteDropdownMenu
import ru.resodostudios.cashsense.core.ui.TimeZoneBroadcastReceiver
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import ru.resodostudios.cashsense.core.ui.R as uiR

fun LazyGridScope.transactions(
    transactionsWithCategories: List<TransactionWithCategory>,
    currency: Currency,
    onDelete: (Transaction) -> Unit
) {
    var currentDate: String? = null
    val sortedTransactions = transactionsWithCategories.sortedByDescending { it.transaction.date }

    items(sortedTransactions) { transactionWithCategory ->
        Column {
            if (currentDate != dateFormatted(date = transactionWithCategory.transaction.date)) {
                currentDate = dateFormatted(date = transactionWithCategory.transaction.date)
                Text(
                    text = currentDate!!,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
            ListItem(
                headlineContent = {
                    AmountWithCurrencyText(
                        amount = transactionWithCategory.transaction.amount,
                        currency = currency
                    )
                },
                trailingContent = {
                    EditAndDeleteDropdownMenu(
                        onEdit = { /*TODO*/ },
                        onDelete = { onDelete(transactionWithCategory.transaction) }
                    )
                },
                supportingContent = {
                    Text(
                        text = if (transactionWithCategory.category.title == null) stringResource(uiR.string.none) else transactionWithCategory.category.title.toString()
                    )
                }
            )
        }
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
        .ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale.getDefault())
        .withZone(zoneId)
        .format(date.toJavaInstant())
}