package ru.resodostudios.cashsense.core.model.data

import kotlinx.datetime.Instant
import java.math.BigDecimal
import kotlin.uuid.Uuid

data class Transaction(
    val id: String,
    val walletOwnerId: String,
    val description: String?,
    val amount: BigDecimal,
    val timestamp: Instant,
    val status: StatusType,
    val ignored: Boolean,
    val transferId: Uuid?,
)