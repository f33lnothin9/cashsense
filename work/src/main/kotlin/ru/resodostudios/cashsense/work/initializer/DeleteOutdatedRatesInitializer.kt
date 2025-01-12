package ru.resodostudios.cashsense.work.initializer

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import ru.resodostudios.cashsense.work.worker.DeleteOutdatedRatesWorker

object DeleteOutdatedRates {

    fun initialize(context: Context) {
        WorkManager.getInstance(context).apply {
            enqueueUniquePeriodicWork(
                DELETE_OUTDATED_RATES_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                DeleteOutdatedRatesWorker.periodicDeleteOutdatedRatesWork(),
            )
        }
    }
}

// This name should not be changed otherwise the app may have concurrent sync requests running
internal const val DELETE_OUTDATED_RATES_WORK_NAME = "DeleteOutdatedRatesWorkName"