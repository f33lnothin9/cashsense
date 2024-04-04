package ru.resodostudios.cashsense.core.ui

import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.Currency
import java.util.Locale

fun BigDecimal.formatAmount(currency: String, withPlus: Boolean = false): String {
    val currencyFormat = DecimalFormat.getCurrencyInstance(Locale.getDefault())
    val customCurrency = Currency.getInstance(currency)
    currencyFormat.currency = customCurrency

    val formattedAmount = currencyFormat.format(this)

    return if (withPlus && this > BigDecimal.ZERO) "+$formattedAmount" else formattedAmount
}