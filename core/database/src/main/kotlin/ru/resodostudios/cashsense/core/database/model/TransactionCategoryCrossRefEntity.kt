package ru.resodostudios.cashsense.core.database.model

import androidx.room.Entity
import java.util.UUID

@Entity(
    tableName = "transaction_category_cross_ref",
    primaryKeys = ["transactionId", "categoryId"]
)
data class TransactionCategoryCrossRefEntity(
    val transactionId: UUID,
    val categoryId: Long
)