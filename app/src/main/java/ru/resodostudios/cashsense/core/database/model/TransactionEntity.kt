package ru.resodostudios.cashsense.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
)
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val category: String,
    val value: Int
)