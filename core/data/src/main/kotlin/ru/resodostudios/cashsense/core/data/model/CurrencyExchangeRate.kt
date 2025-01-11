package ru.resodostudios.cashsense.core.data.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import ru.resodostudios.cashsense.core.database.model.CurrencyExchangeRateEntity
import ru.resodostudios.cashsense.core.network.model.NetworkCurrencyExchangeRate
import java.math.BigDecimal
import java.util.Currency
import kotlin.uuid.Uuid

fun NetworkCurrencyExchangeRate.asEntity() =
    CurrencyExchangeRateEntity(
        uuid = Uuid.random().toHexString(),
        baseCurrency = Currency.getInstance(base),
        targetCurrency = Currency.getInstance(target),
        exchangeRate = exchangeRate?.toBigDecimal() ?: BigDecimal.ZERO,
        timestamp = Instant.parse(timestamp ?: Clock.System.now().toString()),
    )