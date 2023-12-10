package ru.resodostudios.cashsense.core.data.model

import ru.resodostudios.cashsense.core.database.model.CategoryEntity
import ru.resodostudios.cashsense.core.database.model.TransactionEntity
import ru.resodostudios.cashsense.core.database.model.TransactionWithCategoryEntity
import ru.resodostudios.cashsense.core.database.model.WalletEntity
import ru.resodostudios.cashsense.core.database.model.WalletWithTransactionsAndCategoriesEntity
import ru.resodostudios.cashsense.core.model.data.WalletWithTransactionsAndCategories

fun WalletWithTransactionsAndCategories.asEntity() = WalletWithTransactionsAndCategoriesEntity(
    wallet = WalletEntity(
        walletId = wallet.walletId,
        title = wallet.title,
        startBalance = wallet.startBalance,
        currency = wallet.currency,
        income = wallet.income,
        expenses = wallet.expenses
    ),
    transactionsWithCategories = transactionsWithCategories.map {
        TransactionWithCategoryEntity(
            transaction = TransactionEntity(
                transactionId = it.transaction.transactionId,
                walletOwnerId = it.transaction.walletOwnerId,
                categoryId = it.transaction.categoryId,
                description = it.transaction.description,
                amount = it.transaction.amount,
                date = it.transaction.date
            ),
            category = CategoryEntity(
                id = it.category?.id,
                title = it.category?.title,
                icon = it.category?.icon
            )
        )
    }
)