package ru.resodostudios.cashsense.core.data.util

import ru.resodostudios.cashsense.core.model.data.Reminder

interface ReminderScheduler {

    fun schedule(reminder: Reminder)

    fun cancel(reminderId: Int)
}