package ru.resodostudios.cashsense.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import ru.resodostudios.cashsense.core.model.data.Currency
import java.text.NumberFormat

@Composable
fun AmountWithCurrencyText(
    amount: Double,
    currency: Currency
) {
    Text(
        text = "${getFormattedAmount(amount)} ${currency.symbol}",
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.bodyLarge
    )
}

private fun getFormattedAmount(amount: Double): String =
    NumberFormat.getInstance().format(amount)