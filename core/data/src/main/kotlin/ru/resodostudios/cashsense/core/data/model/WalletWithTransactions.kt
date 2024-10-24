package ru.resodostudios.cashsense.core.data.model

import ru.resodostudios.cashsense.core.database.model.PopulatedTransaction
import ru.resodostudios.cashsense.core.database.model.PopulatedWallet
import ru.resodostudios.cashsense.core.model.data.WalletWithTransactionsAndCategories

fun WalletWithTransactionsAndCategories.asEntity() = PopulatedWallet(
    wallet = wallet.asEntity(),
    transactions = transactionsWithCategories.map {
        PopulatedTransaction(
            transaction = it.transaction.asEntity(),
            category = it.category?.asEntity()
        )
    }
)