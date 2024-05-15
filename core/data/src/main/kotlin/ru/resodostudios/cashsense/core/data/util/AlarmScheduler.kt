package ru.resodostudios.cashsense.core.data.util

import ru.resodostudios.cashsense.core.model.data.Reminder

interface AlarmScheduler {

    fun schedule(reminder: Reminder)

    fun cancel(reminder: Reminder)
}