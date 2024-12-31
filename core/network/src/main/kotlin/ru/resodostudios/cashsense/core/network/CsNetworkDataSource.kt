package ru.resodostudios.cashsense.core.network

import ru.resodostudios.cashsense.core.network.model.NetworkCurrencyRate

interface CsNetworkDataSource {

    suspend fun getCurrencyRate(baseCurrencyCode: String, targetCurrencyCode: String): NetworkCurrencyRate
}
