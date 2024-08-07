package ru.resodostudios.cashsense.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import ru.resodostudios.cashsense.core.model.data.StatusType
import ru.resodostudios.cashsense.core.model.data.Transaction
import java.math.BigDecimal

@Entity(
    tableName = "transactions",
)
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "wallet_owner_id")
    val walletOwnerId: String,
    val description: String?,
    val amount: BigDecimal,
    val timestamp: Instant,
    @ColumnInfo(defaultValue = "COMPLETED")
    val status: StatusType,
    @ColumnInfo(defaultValue = "0")
    val ignored: Boolean,
)

fun TransactionEntity.asExternalModel() = Transaction(
    id = id,
    walletOwnerId = walletOwnerId,
    description = description,
    amount = amount,
    timestamp = timestamp,
    status = status,
    ignored = ignored,
)