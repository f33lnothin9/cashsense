package ru.resodostudios.cashsense.core.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.resodostudios.cashsense.feature.wallet.domain.model.Transaction
import java.time.LocalDateTime

@Entity(
    tableName = "transactions",
)
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val category: String,
    val value: Int,
    @Embedded
    val timestamp: LocalDateTime
)

fun TransactionEntity.asExternalModel() = Transaction(
    id = id,
    name = name,
    category = category,
    value = value,
    timestamp = timestamp
)