package ru.resodostudios.cashsense.core.ui

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

fun getFormattedAmountAndCurrency(amount: Double, currencyName: String): String {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    val customCurrency = Currency.getInstance(currencyName)
    currencyFormat.currency = customCurrency

    return currencyFormat.format(amount)
}

fun getFormattedAmount(amount: Double): String {
    val amountFormat = NumberFormat.getInstance(Locale.getDefault())

    return amountFormat.format(amount)
}