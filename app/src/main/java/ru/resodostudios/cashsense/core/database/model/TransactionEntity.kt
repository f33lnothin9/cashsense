package ru.resodostudios.cashsense.core.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.resodostudios.cashsense.core.model.data.Transaction
import java.time.LocalDateTime

@Entity(
    tableName = "transactions",
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val description: String?,
    val category: String?,
    val value: Int,
    @Embedded
    val timestamp: LocalDateTime
)

fun TransactionEntity.asExternalModel() = Transaction(
    id = id,
    name = name,
    description = description,
    category = category,
    value = value,
    timestamp = timestamp
)