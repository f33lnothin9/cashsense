package ru.resodostudios.cashsense.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.model.data.WalletWithTransactionsAndCategories

data class WalletWithTransactionsAndCategoriesEntity(
    @Embedded
    val wallet: WalletEntity,
    @Relation(
        entity = TransactionEntity::class,
        parentColumn = "walletId",
        entityColumn = "walletOwnerId"
    )
    val transactionsWithCategories: List<TransactionWithCategoryEntity>
)

fun WalletWithTransactionsAndCategoriesEntity.asExternalModel() = WalletWithTransactionsAndCategories(
    wallet = Wallet(
        walletId = wallet.walletId,
        title = wallet.title,
        startBalance = wallet.startBalance,
        currency = wallet.currency,
        income = wallet.income,
        expenses = wallet.expenses
    ),
    transactionsWithCategories = transactionsWithCategories.map {
        TransactionWithCategory(
            transaction = Transaction(
                transactionId = it.transaction.transactionId,
                walletOwnerId = it.transaction.walletOwnerId,
                categoryId = it.transaction.categoryId,
                description = it.transaction.description,
                amount = it.transaction.amount,
                date = it.transaction.date
            ),
            category = Category(
                id = it.category?.id,
                title = it.category?.title,
                icon = it.category?.icon
            )
        )

    }
)