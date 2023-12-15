package ru.resodostudios.cashsense.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.database.model.TransactionCategoryCrossRefEntity
import ru.resodostudios.cashsense.core.database.model.TransactionEntity
import ru.resodostudios.cashsense.core.database.model.TransactionWithCategoryEntity

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions WHERE transactionId = :transactionId")
    fun getTransactionEntity(transactionId: String): Flow<TransactionEntity>

    @Query("SELECT * FROM transactions ORDER BY transactionId DESC")
    fun getTransactionEntities(): Flow<List<TransactionEntity>>

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getTransactionWithCategoryEntities(): List<TransactionWithCategoryEntity>

    @Upsert
    suspend fun upsertTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Upsert
    suspend fun upsertTransactionCategoryCrossRef(crossRef: TransactionCategoryCrossRefEntity)

    @Query(
        value = """
            DELETE FROM transaction_category_cross_ref
            WHERE transactionId = :transactionId
        """
    )
    suspend fun deleteTransactionCategoryCrossRef(transactionId: Long)
}