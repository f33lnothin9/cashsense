package ru.resodostudios.cashsense.core.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TransactionWithCategoryEntity(
    @Embedded
    val transaction: TransactionEntity,
    @Relation(
        parentColumn = "transactionId",
        entityColumn = "categoryId",
        associateBy = Junction(TransactionCategoryCrossRef::class)
    )
    val category: CategoryEntity?
)