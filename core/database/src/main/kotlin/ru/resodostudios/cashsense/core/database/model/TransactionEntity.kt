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
    @ColumnInfo(name = "category_id")
    val categoryId: String?,
    val description: String?,
    val amount: BigDecimal,
    val date: Instant
)

fun TransactionEntity.asExternalModel() = Transaction(
    id = id,
    walletOwnerId = walletOwnerId,
    categoryId = categoryId,
    description = description,
    amount = amount,
    date = date
)