package ru.resodostudios.cashsense.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import ru.resodostudios.cashsense.core.model.data.Currency
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AmountWithCurrencyText(
    amount: Double,
    currency: Currency
) {
    Text(
        text = getFormattedAmountWithCurrency(amount, currency),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.bodyLarge
    )
}

private fun getFormattedAmountWithCurrency(amount: Double, currency: Currency): String {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    val customCurrency = java.util.Currency.getInstance(currency.name)
    currencyFormat.currency = customCurrency

    return currencyFormat.format(amount)
}