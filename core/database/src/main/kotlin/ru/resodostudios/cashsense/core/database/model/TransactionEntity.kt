package ru.resodostudios.cashsense.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import ru.resodostudios.cashsense.core.model.data.Transaction
import java.math.BigDecimal

@Entity(
    tableName = "transactions"
)
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "wallet_owner_id")
    val walletOwnerId: String,
    val description: String?,
    val amount: BigDecimal,
    val timestamp: Instant,
)

fun TransactionEntity.asExternalModel() = Transaction(
    id = id,
    walletOwnerId = walletOwnerId,
    description = description,
    amount = amount,
    timestamp = timestamp,
)