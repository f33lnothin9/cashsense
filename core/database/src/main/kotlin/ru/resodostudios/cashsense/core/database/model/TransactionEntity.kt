package ru.resodostudios.cashsense.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import ru.resodostudios.cashsense.core.model.data.Transaction

@Entity(
    tableName = "transactions",
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val transactionId: Long,
    val walletId: Long,
    val description: String?,
    val category: String?,
    val value: Int,
    val date: Instant
)

fun TransactionEntity.asExternalModel() = Transaction(
    transactionId = transactionId,
    walletId = walletId,
    description = description,
    category = category,
    value = value,
    date = date
)