package ru.resodostudios.cashsense.core.model.data

import kotlinx.datetime.Instant
import java.math.BigDecimal
import java.util.Currency

data class Subscription(
    val id: String,
    val title: String,
    val amount: BigDecimal,
    val currency: Currency,
    val paymentDate: Instant,
    val reminder: Reminder?,
)