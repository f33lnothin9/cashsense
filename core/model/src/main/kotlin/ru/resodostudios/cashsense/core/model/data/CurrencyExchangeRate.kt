package ru.resodostudios.cashsense.core.model.data

import java.math.BigDecimal
import java.util.Currency

data class CurrencyExchangeRate(
    val baseCurrency: Currency,
    val targetCurrency: Currency,
    val exchangeRate: BigDecimal,
)
