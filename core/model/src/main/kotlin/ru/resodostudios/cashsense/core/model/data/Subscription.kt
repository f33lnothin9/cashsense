package ru.resodostudios.cashsense.core.model.data

import kotlinx.datetime.Instant
import java.math.BigDecimal
import java.util.UUID

data class Subscription(
    val subscriptionId: UUID = UUID.randomUUID(),
    val title: String,
    val amount: BigDecimal,
    val paymentDate: Instant,
    val notificationDate: Instant?
)