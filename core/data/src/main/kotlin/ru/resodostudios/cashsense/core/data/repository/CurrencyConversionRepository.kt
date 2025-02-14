package ru.resodostudios.cashsense.core.data.repository

import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.model.data.CurrencyExchangeRate
import java.util.Currency

interface CurrencyConversionRepository {

    fun getConvertedCurrencies(
        baseCurrencies: Set<Currency>,
        targetCurrency: Currency,
    ): Flow<List<CurrencyExchangeRate>>

    suspend fun deleteOutdatedCurrencyExchangeRates()
}