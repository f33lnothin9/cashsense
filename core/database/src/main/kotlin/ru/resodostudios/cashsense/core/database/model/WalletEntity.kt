package ru.resodostudios.cashsense.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.resodostudios.cashsense.core.model.data.Currency

@Entity(
    tableName = "wallets"
)
data class WalletEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val title: String,
    val startBalance: Float,
    val currency: Currency,
    val income: Float,
    val expenses: Float
)
