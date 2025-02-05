package ru.resodostudios.cashsense.core.data.util

import com.google.android.play.core.ktx.AppUpdateResult
import kotlinx.coroutines.flow.Flow

interface InAppUpdateManager {

    val appUpdateResult: Flow<AppUpdateResult>
}