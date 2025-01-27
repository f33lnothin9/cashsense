package ru.resodostudios.cashsense.core.database.model

import androidx.room.Embedded
import androidx.room.Relation
import ru.resodostudios.cashsense.core.model.data.ExtendedWallet
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory

data class PopulatedWallet(
    @Embedded
    val wallet: WalletEntity,
    @Relation(
        entity = TransactionEntity::class,
        parentColumn = "id",
        entityColumn = "wallet_owner_id",
    )
    val transactions: List<PopulatedTransaction>,
)

fun PopulatedWallet.asExternalModel() = ExtendedWallet(
    wallet = wallet.asExternalModel(),
    transactionsWithCategories = transactions.map {
        TransactionWithCategory(
            transaction = Transaction(
                id = it.transaction.id,
                amount = it.transaction.amount,
                timestamp = it.transaction.timestamp,
                ignored = it.transaction.ignored,
                transferId = it.transaction.transferId,
                walletOwnerId = it.transaction.walletOwnerId,
                description = it.transaction.description,
                status = it.transaction.status,
                currency = wallet.currency,
            ),
            category = it.category?.asExternalModel(),
        )
    }
)