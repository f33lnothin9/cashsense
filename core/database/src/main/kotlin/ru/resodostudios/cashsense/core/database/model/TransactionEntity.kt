package ru.resodostudios.cashsense.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import ru.resodostudios.cashsense.core.model.data.Transaction
import java.util.UUID

@Entity(
    tableName = "transactions"
)
data class TransactionEntity(
    @PrimaryKey
    val transactionId: UUID,
    val walletOwnerId: Long,
    val categoryOwnerId: Long?,
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