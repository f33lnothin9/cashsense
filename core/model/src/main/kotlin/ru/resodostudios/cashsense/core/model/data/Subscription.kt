package ru.resodostudios.cashsense.core.model.data

import kotlinx.datetime.Instant
import java.math.BigDecimal

data class Subscription(
    val id: String,
    val title: String,
    val amount: BigDecimal,
    val currency: String,
    val paymentDate: Instant,
    val notificationDate: Instant?,
    val repeatingInterval: Long?
)