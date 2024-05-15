package ru.resodostudios.cashsense.core.data.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.resodostudios.cashsense.core.model.data.Reminder
import javax.inject.Inject

internal class NotificationAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    private fun createPendingIntent(reminder: Reminder): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_REMINDER_ID, reminder.id)
        }

        return PendingIntent.getBroadcast(
            context,
            reminder.id ?: 0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    override fun schedule(reminder: Reminder) {
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            reminder.notificationDate?.toEpochMilliseconds() ?: 0L,
            reminder.repeatingInterval ?: 0,
            createPendingIntent(reminder),
        )
    }

    override fun cancel(reminder: Reminder) {
        alarmManager.cancel(
            createPendingIntent(reminder)
        )
    }
}

internal const val EXTRA_REMINDER_ID = "reminder-id"