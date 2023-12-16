package ru.resodostudios.cashsense.core.model.data

import java.util.UUID

data class TransactionCategoryCrossRef(
    val transactionId: UUID,
    val categoryId: Long
)