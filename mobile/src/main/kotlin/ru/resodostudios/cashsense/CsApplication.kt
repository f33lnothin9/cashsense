package ru.resodostudios.cashsense

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ru.resodostudios.cashsense.work.initializer.DeleteOutdatedRates

@HiltAndroidApp
class CsApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        DeleteOutdatedRates.initialize(context = this)
    }
}