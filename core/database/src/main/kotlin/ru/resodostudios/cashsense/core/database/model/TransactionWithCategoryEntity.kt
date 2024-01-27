package ru.resodostudios.cashsense.core.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TransactionWithCategoryEntity(
    @Embedded
    val transaction: TransactionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = TransactionCategoryCrossRefEntity::class,
            parentColumn = "transaction_id",
            entityColumn = "category_id"
        )
    )
    val category: CategoryEntity?
)