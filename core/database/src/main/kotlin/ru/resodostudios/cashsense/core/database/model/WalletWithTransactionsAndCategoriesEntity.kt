package ru.resodostudios.cashsense.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.model.data.WalletWithTransactionsAndCategories

data class WalletWithTransactionsAndCategoriesEntity(
    @Embedded
    val wallet: WalletEntity,
    @Relation(
        entity = TransactionEntity::class,
        parentColumn = "id",
        entityColumn = "wallet_owner_id",
    )
    val transactions: List<TransactionWithCategoryEntity>,
)

fun WalletWithTransactionsAndCategoriesEntity.asExternalModel() = WalletWithTransactionsAndCategories(
    wallet = wallet.asExternalModel(),
    transactionsWithCategories = transactions.map {
        TransactionWithCategory(
            transaction = it.transaction.asExternalModel(),
            category = it.category?.asExternalModel() ?: Category(),
        )
    }
)