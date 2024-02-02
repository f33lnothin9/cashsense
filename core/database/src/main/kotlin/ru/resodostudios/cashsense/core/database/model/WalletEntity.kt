package ru.resodostudios.cashsense.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.resodostudios.cashsense.core.model.data.Wallet
import java.math.BigDecimal

@Entity(
    tableName = "wallets"
)
data class WalletEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    @ColumnInfo(name = "initial_balance")
    val initialBalance: BigDecimal,
    val currency: String
)

fun WalletEntity.asExternalModel() = Wallet(
    id = id,
    title = title,
    initialBalance = initialBalance,
    currency = currency
)
