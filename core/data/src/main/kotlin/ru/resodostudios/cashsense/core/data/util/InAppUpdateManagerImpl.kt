package ru.resodostudios.cashsense.core.data.util

import android.app.Activity
import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.requestUpdateFlow
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class InAppUpdateManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : InAppUpdateManager {

    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(context)

    override val inAppUpdateResult: Flow<InAppUpdateResult> = appUpdateManager.requestUpdateFlow()
        .map(AppUpdateResult::toInAppUpdateResult)
        .catch { InAppUpdateResult.NotAvailable }
}

sealed class InAppUpdateResult {
    data class Available(val result: AppUpdateResult.Available) : InAppUpdateResult() {
        fun startFlexibleUpdate(activity: Activity, requestCode: Int): Boolean =
            result.startFlexibleUpdate(activity, requestCode)
    }

    data class Downloaded(val result: AppUpdateResult.Downloaded) : InAppUpdateResult() {
        suspend fun completeUpdate() = result.completeUpdate()
    }

    data object NotAvailable : InAppUpdateResult()
}

private fun AppUpdateResult.toInAppUpdateResult(): InAppUpdateResult = when (this) {
    is AppUpdateResult.Available -> InAppUpdateResult.Available(this)
    is AppUpdateResult.Downloaded -> InAppUpdateResult.Downloaded(this)
    else -> InAppUpdateResult.NotAvailable
}