package ru.resodostudios.cashsense.core.database.model

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
    val description: String?,
    val category: String?,
    val value: Int,
    val date: LocalDateTime
)

fun TransactionEntity.asExternalModel() = Transaction(
    id = id,
    description = description,
    category = category,
    value = value,
    date = date
)