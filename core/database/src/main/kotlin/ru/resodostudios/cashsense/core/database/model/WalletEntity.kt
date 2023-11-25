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
    val id: Int,
    val title: String,
    val startBalance: Float,
    val currency: Currency,
    val income: Float,
    val expenses: Float
)

fun WalletEntity.asExternalModel() = Wallet(
    id = id,
    title = title,
    startBalance = startBalance,
    currency = currency,
    income = income,
    expenses = expenses
)
