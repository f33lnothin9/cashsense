package ru.resodostudios.cashsense.core.model.data

import kotlinx.datetime.Instant
import java.util.UUID

data class Transaction(
    val transactionId: UUID,
    val walletOwnerId: Long,
    val categoryOwnerId: Long? = null,
    val description: String?,
    val amount: Double,
    val date: Instant
)