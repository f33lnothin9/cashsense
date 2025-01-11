package ru.resodostudios.cashsense.work.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import ru.resodostudios.cashsense.core.database.dao.CurrencyConversionDao
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.days

@HiltWorker
internal class DeleteOutdatedRatesWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val currencyConversionDao: CurrencyConversionDao,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val cutoff = Clock.System.now().minus(3.days)
        val outdatedRates = currencyConversionDao.getOutdatedCurrencyExchangeRateEntities(cutoff)
            .firstOrNull()
        outdatedRates?.let { currencyConversionDao.deleteCurrencyExchangeRates(it) }
        return Result.success()
    }

    companion object {

        fun periodicDeleteOutdatedRatesWork() =
            PeriodicWorkRequestBuilder<DelegatingWorker>(
                repeatInterval = 2,
                repeatIntervalTimeUnit = TimeUnit.DAYS,
            )
                .setInputData(DeleteOutdatedRatesWorker::class.delegatedData())
                .build()
    }
}