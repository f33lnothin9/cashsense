package ru.resodostudios.cashsense.feature.home.domain.model

import ru.resodostudios.cashsense.core.database.model.TransactionEntity
import java.time.LocalDateTime

data class Transaction(
    val id: Int,
    val name: String,
    val category: String,
    val value: Int,
    val timestamp: LocalDateTime
)

fun Transaction.asEntity() = TransactionEntity(
    id = id,
    name = name,
    category = category,
    value = value,
    timestamp = timestamp
)