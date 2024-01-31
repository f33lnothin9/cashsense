package ru.resodostudios.cashsense.core.ui

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

fun getFormattedAmountAndCurrency(amount: BigDecimal, currencyName: String): String {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    val customCurrency = Currency.getInstance(currencyName)
    currencyFormat.currency = customCurrency

    return currencyFormat.format(amount)
}