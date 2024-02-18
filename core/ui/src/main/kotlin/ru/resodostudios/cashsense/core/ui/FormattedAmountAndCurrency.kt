package ru.resodostudios.cashsense.core.ui

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

fun BigDecimal.formatAmountWithCurrency(currency: String, withPlus: Boolean = false): String {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    val customCurrency = Currency.getInstance(currency)
    currencyFormat.currency = customCurrency

    return if (withPlus) {
        "${if (this > BigDecimal(0)) "+" else ""}${currencyFormat.format(this)}"
    } else {
        currencyFormat.format(this)
    }
}