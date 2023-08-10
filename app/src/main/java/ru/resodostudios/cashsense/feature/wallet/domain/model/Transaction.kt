package ru.resodostudios.cashsense.feature.wallet.domain.model

import java.time.LocalDateTime

data class Transaction(
    val id: Int,
    val name: String,
    val category: String,
    val value: Int,
    val timestamp: LocalDateTime
)