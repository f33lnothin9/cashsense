package ru.resodostudios.cashsense.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory

data class TransactionWithCategoryEntity(
    @Embedded
    val transaction: TransactionEntity,
    @Relation(
        parentColumn = "transactionId",
        entityColumn = "categoryId"
    )
    val category: CategoryEntity
)

fun TransactionWithCategoryEntity.asExternalModel() = TransactionWithCategory(
    transaction = Transaction(
        transactionId = transaction.transactionId,
        walletOwnerId = transaction.walletOwnerId,
        description = transaction.description,
        amount = transaction.amount,
        date = transaction.date
    ),
    category = Category(
        categoryId = category.categoryId,
        title = category.title,
        icon = category.icon
    )
)