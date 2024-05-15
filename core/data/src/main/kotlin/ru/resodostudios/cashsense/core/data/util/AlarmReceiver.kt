package ru.resodostudios.cashsense.core.data.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.SubscriptionsRepository
import ru.resodostudios.cashsense.core.notifications.Notifier
import javax.inject.Inject

@AndroidEntryPoint
internal class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var subscriptionsRepository: SubscriptionsRepository

    @Inject
    lateinit var notificationAlarmScheduler: NotificationAlarmScheduler

    @Inject
    lateinit var notifier: Notifier

    override fun onReceive(context: Context?, intent: Intent?) {

        val reminderId = intent?.getIntExtra(EXTRA_REMINDER_ID, 0)

        CoroutineScope(Dispatchers.IO).launch {
            subscriptionsRepository.getSubscriptions().collect { subscriptions ->
                val subscription = subscriptions.find { it.id.hashCode() == reminderId }

                subscription?.let {
                    notifier.postSubscriptionNotification(it)
                }
            }
        }

        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                subscriptionsRepository.getSubscriptions().collect { subscriptions ->
                    subscriptions
                        .filter { it.reminder != null }
                        .forEach { subscription ->
                            subscription.reminder?.let {
                                notificationAlarmScheduler.schedule(it)
                            }
                        }
                }
            }
        }
    }
}