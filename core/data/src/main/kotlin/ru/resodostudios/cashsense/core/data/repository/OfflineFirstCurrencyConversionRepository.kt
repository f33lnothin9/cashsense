package ru.resodostudios.cashsense.core.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Clock
import ru.resodostudios.cashsense.core.data.model.asEntity
import ru.resodostudios.cashsense.core.database.dao.CurrencyConversionDao
import ru.resodostudios.cashsense.core.database.model.asExternalModel
import ru.resodostudios.cashsense.core.model.data.CurrencyExchangeRate
import ru.resodostudios.cashsense.core.network.CsNetworkDataSource
import java.util.Currency
import javax.inject.Inject
import kotlin.time.Duration.Companion.days

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
            .map { cachedRates ->
                cachedRates.map { it.asExternalModel() }
            }
            .onEach { currencyExchangeRates ->
                val cachedBaseCurrencies = currencyExchangeRates
                    .mapTo(HashSet()) { it.baseCurrency }
                val missingBaseCurrencies = buildSet {
                    addAll(baseCurrencies)
                    remove(targetCurrency)
                    removeAll(cachedBaseCurrencies)
                }
                if (missingBaseCurrencies.isNotEmpty()) {
                    getCurrencyExchangeRates(missingBaseCurrencies, targetCurrency)
                }
            }
            .catch { emit(emptyList()) }

    override suspend fun deleteOutdatedCurrencyExchangeRates() {
        val cutoff = Clock.System.now().minus(3.days)
        dao.deleteOutdatedCurrencyExchangeRates(cutoff)
    }

    private suspend fun getCurrencyExchangeRates(
        baseCurrencies: Set<Currency>,
        targetCurrency: Currency,
    ) = coroutineScope {
        baseCurrencies.map { baseCurrency ->
            async {
                network.getCurrencyExchangeRate(
                    baseCurrencyCode = baseCurrency.currencyCode,
                    targetCurrencyCode = targetCurrency.currencyCode,
                )
            }
        }.awaitAll()
    }.also { remoteData ->
        dao.upsertCurrencyExchangeRates(remoteData.map { it.asEntity() })
    }
}
