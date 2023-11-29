package ru.resodostudios.cashsense.core.model.data

import kotlinx.datetime.Instant

data class Transaction(
    val transactionId: Long = 0L,
    val walletId: Long,
    val description: String?,
    val category: String?,
    val value: Int,
    val date: Instant
)