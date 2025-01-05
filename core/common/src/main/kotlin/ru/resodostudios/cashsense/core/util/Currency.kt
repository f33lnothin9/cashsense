package ru.resodostudios.cashsense.core.util

import ru.resodostudios.cashsense.core.util.Constants.USD_CURRENCY_CODE
import java.util.Currency
import java.util.Locale

fun getValidCurrencies(): List<Currency> {
    return Currency.getAvailableCurrencies()
        .filterNot { it.displayName.contains("""\d+""".toRegex()) }
        .sortedBy { it.currencyCode }
}

fun getDefaultCurrency(): Currency {
    val currencyByLocale = Currency.getInstance(Locale.getDefault())
    val defaultCurrency = getValidCurrencies()
        .find { it == currencyByLocale } ?: Currency.getInstance(USD_CURRENCY_CODE)
    return defaultCurrency
}

fun getUsdCurrency(): Currency = Currency.getInstance(USD_CURRENCY_CODE)