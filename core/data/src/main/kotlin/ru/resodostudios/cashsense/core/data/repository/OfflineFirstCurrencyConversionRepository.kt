package ru.resodostudios.cashsense.core.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.resodostudios.cashsense.core.data.model.asEntity
import ru.resodostudios.cashsense.core.data.model.asExternalModel
import ru.resodostudios.cashsense.core.database.dao.CurrencyConversionDao
import ru.resodostudios.cashsense.core.database.model.asExternalModel
import ru.resodostudios.cashsense.core.model.data.CurrencyExchangeRate
import ru.resodostudios.cashsense.core.network.CsDispatchers.IO
import ru.resodostudios.cashsense.core.network.CsNetworkDataSource
import ru.resodostudios.cashsense.core.network.Dispatcher
import java.util.Currency
import javax.inject.Inject

internal class OfflineFirstCurrencyConversionRepository @Inject constructor(
    private val dao: CurrencyConversionDao,
    private val network: CsNetworkDataSource,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : CurrencyConversionRepository {

    override fun getConvertedCurrencies(
        baseCurrencies: Set<Currency>,
        targetCurrency: Currency,
    ): Flow<List<CurrencyExchangeRate>> = flow {
        val localData = dao.getCurrencyExchangeRateEntities(
            targetCurrency = targetCurrency,
            baseCurrencies = baseCurrencies,
        ).firstOrNull()

        if (!localData.isNullOrEmpty()) {
            emit(localData.map { it.asExternalModel() })
        } else {
            val remoteData = try {
                baseCurrencies.map { baseCurrency ->
                    network.getCurrencyExchangeRate(
                        baseCurrency.currencyCode,
                        targetCurrency.currencyCode,
                    )
                }
            } catch (e: Exception) {
                null
            }
            if (!remoteData.isNullOrEmpty()) {
                dao.upsertCurrencyExchangeRates(remoteData.map { it.asEntity() })
                emit(remoteData.map { it.asExternalModel() })
            }
        }
    }.flowOn(ioDispatcher)
}