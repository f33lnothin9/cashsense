package ru.resodostudios.cashsense.core.data.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        println("Notification triggered")
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            println("Device booted")
        }
    }
}