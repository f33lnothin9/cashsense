package ru.resodostudios.cashsense.core.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class TransactionWithCategoryEntity(
    @Embedded
    val transaction: TransactionEntity,
    @Relation(
        parentColumn = "transactionId",
        entityColumn = "id"
    )
    val category: CategoryEntity?
)