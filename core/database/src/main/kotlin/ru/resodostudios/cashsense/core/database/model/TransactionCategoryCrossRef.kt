package ru.resodostudios.cashsense.core.database.model

import androidx.room.Entity

@Entity(
    tableName = "transaction_category_cross_ref",
    primaryKeys = ["transactionId", "categoryId"]
)
data class TransactionCategoryCrossRef(
    val transactionId: Long,
    val categoryId: Long
)