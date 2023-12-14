package ru.resodostudios.cashsense.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import ru.resodostudios.cashsense.core.model.data.Transaction

@Entity(
    tableName = "transactions"
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val transactionId: Long,
    val walletOwnerId: Long,
    val description: String?,
    val amount: Double,
    val date: Instant
)

fun TransactionEntity.asExternalModel() = Transaction(
    transactionId = transactionId,
    walletOwnerId = walletOwnerId,
    description = description,
    amount = amount,
    date = date
)