package ru.resodostudios.cashsense.core.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import ru.resodostudios.cashsense.core.model.data.Transaction

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["transactionId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val transactionId: Long,
    val walletOwnerId: Long,
    val categoryId: Long?,
    val description: String?,
    val amount: Float,
    val date: Instant
)

fun TransactionEntity.asExternalModel() = Transaction(
    transactionId = transactionId,
    walletOwnerId = walletOwnerId,
    categoryId = categoryId,
    description = description,
    amount = amount,
    date = date
)