package ru.resodostudios.cashsense.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.database.model.TransactionEntity

@Dao
interface TransactionDao {

    @Query(
        value = """
        SELECT * FROM transactions
        WHERE transactionId = :transactionId
    """,
    )
    fun getTransactionEntity(transactionId: String): Flow<TransactionEntity>

    @Query(value = "SELECT * FROM transactions ORDER BY transactionId DESC")
    fun getTransactionEntities(): Flow<List<TransactionEntity>>

    @Upsert
    suspend fun upsertTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)
}