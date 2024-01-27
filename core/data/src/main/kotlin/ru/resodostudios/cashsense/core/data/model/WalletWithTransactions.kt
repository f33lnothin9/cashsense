package ru.resodostudios.cashsense.core.data.model

import ru.resodostudios.cashsense.core.database.model.TransactionWithCategoryEntity
import ru.resodostudios.cashsense.core.database.model.WalletWithTransactionsAndCategoriesEntity
import ru.resodostudios.cashsense.core.model.data.WalletWithTransactionsAndCategories

fun WalletWithTransactionsAndCategories.asEntity() = WalletWithTransactionsAndCategoriesEntity(
    wallet = wallet.asEntity(),
    transactions = transactionsWithCategories.map {
        TransactionWithCategoryEntity(
            transaction = it.transaction.asEntity(),
            category = it.category?.asEntity()
        )
    }
)