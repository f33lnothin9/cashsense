package ru.resodostudios.cashsense.core.model.data

import kotlinx.datetime.Instant
import java.math.BigDecimal

data class Subscription(
    val title: String,
    val amount: BigDecimal,
    val paymentDate: Instant,
    val notificationDate: Instant?
)