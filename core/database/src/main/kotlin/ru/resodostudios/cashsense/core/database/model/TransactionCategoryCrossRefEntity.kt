package ru.resodostudios.cashsense.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "transactions_categories",
    primaryKeys = ["transaction_id", "category_id"],
    indices = [
        Index(value = ["transaction_id"]),
        Index(value = ["category_id"])
    ]
)
data class TransactionCategoryCrossRefEntity(
    @ColumnInfo(name = "transaction_id")
    val transactionId: String,
    @ColumnInfo(name = "category_id")
    val categoryId: String
)