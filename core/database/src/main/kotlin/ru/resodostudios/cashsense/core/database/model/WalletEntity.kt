package ru.resodostudios.cashsense.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.resodostudios.cashsense.core.model.data.Currency
import ru.resodostudios.cashsense.core.model.data.Wallet
import java.math.BigDecimal

@Entity(
    tableName = "wallets"
)
data class WalletEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    @ColumnInfo(name = "start_balance")
    val startBalance: BigDecimal,
    val currency: Currency
)

fun WalletEntity.asExternalModel() = Wallet(
    id = id,
    title = title,
    startBalance = startBalance,
    currency = currency
)
