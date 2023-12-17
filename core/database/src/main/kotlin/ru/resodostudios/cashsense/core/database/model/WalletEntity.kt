package ru.resodostudios.cashsense.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.resodostudios.cashsense.core.model.data.Currency
import ru.resodostudios.cashsense.core.model.data.Wallet

@Entity(
    tableName = "wallets"
)
data class WalletEntity(
    @PrimaryKey(autoGenerate = true)
    val walletId: Long,
    val title: String,
    val startBalance: Double,
    val currency: Currency
)

fun WalletEntity.asExternalModel() = Wallet(
    walletId = walletId,
    title = title,
    startBalance = startBalance,
    currency = currency
)
