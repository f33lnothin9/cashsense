package ru.resodostudios.cashsense.core.model.data

import kotlinx.datetime.Instant

data class Transaction(
    val transactionId: Long = 0L,
    val walletOwnerId: Long,
    val categoryId: Long? = null,
    val description: String?,
    val amount: Double,
    val date: Instant
)