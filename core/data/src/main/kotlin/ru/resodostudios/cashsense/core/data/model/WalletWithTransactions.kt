package ru.resodostudios.cashsense.core.data.model

import ru.resodostudios.cashsense.core.database.model.TransactionEntity
import ru.resodostudios.cashsense.core.database.model.WalletEntity
import ru.resodostudios.cashsense.core.database.model.WalletWithTransactionsEntity
import ru.resodostudios.cashsense.core.model.data.WalletWithTransactions

fun WalletWithTransactions.asEntity() = WalletWithTransactionsEntity(
    wallet = WalletEntity(
        walletId = wallet.walletId,
        title = wallet.title,
        startBalance = wallet.startBalance,
        currency = wallet.currency,
        income = wallet.income,
        expenses = wallet.expenses
    ),
    transactions = transactions.map {
        TransactionEntity(
            transactionId = it.transactionId,
            walletOwnerId = it.walletOwnerId,
            description = it.description,
            category = it.category,
            value = it.value,
            date = it.date
        )
    }
)