package ru.resodostudios.cashsense.core.model.data

import kotlinx.datetime.Instant
import java.math.BigDecimal

data class Transaction(
    val id: String,
    val walletOwnerId: String,
    val description: String?,
    val amount: BigDecimal,
    val date: Instant,
)