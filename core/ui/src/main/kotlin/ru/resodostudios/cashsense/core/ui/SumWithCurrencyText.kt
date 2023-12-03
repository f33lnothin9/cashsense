package ru.resodostudios.cashsense.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import ru.resodostudios.cashsense.core.model.data.Currency

@Composable
fun SumWithCurrencyText(
    sum: Float,
    currency: Currency
) {
    Text(
        text = "${getUpdatedSum(sum)} ${currency.symbol}",
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.bodyLarge
    )
}

private fun getUpdatedSum(sum: Float): String =
    if (sum % 1.0 == 0.0) sum.toString().dropLast(2) else sum.toString()