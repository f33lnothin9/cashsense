package ru.resodostudios.cashsense.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.resodostudios.cashsense.core.data.model.asEntity
import ru.resodostudios.cashsense.core.database.dao.CurrencyConversionDao
import ru.resodostudios.cashsense.core.database.model.asExternalModel
import ru.resodostudios.cashsense.core.model.data.CurrencyExchangeRate
import ru.resodostudios.cashsense.core.network.CsNetworkDataSource
import java.util.Currency
import javax.inject.Inject

internal class OfflineFirstCurrencyConversionRepository @Inject constructor(
    private val dao: CurrencyConversionDao,
    private val network: CsNetworkDataSource,
) : CurrencyConversionRepository {

    override fun getConvertedCurrencies(
        baseCurrencies: Set<Currency>,
        targetCurrency: Currency,
    ): Flow<List<CurrencyExchangeRate>> =
        dao.getCurrencyExchangeRateEntities(
            targetCurrency = targetCurrency,
            baseCurrencies = baseCurrencies,
        )
            .map { currencyExchangeRateCached ->
                currencyExchangeRateCached.map { it.asExternalModel() }
            }
            .onEach { currencyExchangeRates ->
                if (currencyExchangeRates.isEmpty() || currencyExchangeRates.size < baseCurrencies.size) {
                    try {
                        getCurrencyExchangeRates(baseCurrencies, targetCurrency)
                    } catch (e: Exception) {
                        emptyList()
                    }
                }
            }

    private suspend fun getCurrencyExchangeRates(
        baseCurrencies: Set<Currency>,
        targetCurrency: Currency,
    ) = baseCurrencies
        .map { baseCurrency ->
            network.getCurrencyExchangeRate(
                baseCurrencyCode = baseCurrency.currencyCode,
                targetCurrencyCode = targetCurrency.currencyCode,
            )
        }
        .also { remoteData ->
            dao.upsertCurrencyExchangeRates(remoteData.map { it.asEntity() })
        }
}
