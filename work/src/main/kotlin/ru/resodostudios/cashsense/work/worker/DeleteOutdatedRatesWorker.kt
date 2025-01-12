package ru.resodostudios.cashsense.work.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.resodostudios.cashsense.core.data.repository.CurrencyConversionRepository
import java.util.concurrent.TimeUnit

@HiltWorker
internal class DeleteOutdatedRatesWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val currencyConversionRepository: CurrencyConversionRepository,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        currencyConversionRepository.deleteOutdatedCurrencyExchangeRates()
        return Result.success()
    }

    companion object {

        fun periodicDeleteOutdatedRatesWork() =
            PeriodicWorkRequestBuilder<DelegatingWorker>(
                repeatInterval = 1,
                repeatIntervalTimeUnit = TimeUnit.DAYS,
            )
                .setInputData(DeleteOutdatedRatesWorker::class.delegatedData())
                .build()
    }
}