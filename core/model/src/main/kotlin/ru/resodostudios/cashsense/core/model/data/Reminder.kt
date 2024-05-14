package ru.resodostudios.cashsense.core.model.data

import kotlinx.datetime.Instant

data class Reminder(
    val id: Int? = null,
    val notificationDate: Instant? = null,
    val repeatingInterval: Long? = null,
)