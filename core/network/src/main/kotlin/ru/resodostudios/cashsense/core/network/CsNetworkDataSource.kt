package ru.resodostudios.cashsense.core.network

import ru.resodostudios.cashsense.core.network.model.NetworkCurrencyExchangeRate

interface CsNetworkDataSource {

    suspend fun getCurrencyExchangeRate(
        baseCurrencyCode: String,
        targetCurrencyCode: String,
    ): NetworkCurrencyExchangeRate
}
