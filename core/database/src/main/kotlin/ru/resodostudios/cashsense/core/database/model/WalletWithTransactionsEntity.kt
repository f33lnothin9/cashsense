package ru.resodostudios.cashsense.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.model.data.WalletWithTransactions

data class WalletWithTransactionsEntity(
    @Embedded
    val wallet: WalletEntity,
    @Relation(
        parentColumn = "walletId",
        entityColumn = "walletOwnerId"
    )
    val transactions: List<TransactionEntity>
)

fun WalletWithTransactionsEntity.asExternalModel() = WalletWithTransactions(
    wallet = Wallet(
        walletId = wallet.walletId,
        title = wallet.title,
        startBalance = wallet.startBalance,
        currency = wallet.currency,
        income = wallet.income,
        expenses = wallet.expenses
    ),
    transactions = transactions.map {
        Transaction(
            transactionId = it.transactionId,
            walletOwnerId = it.walletOwnerId,
            description = it.description,
            amount = it.amount,
            date = it.date
        )
    }
)