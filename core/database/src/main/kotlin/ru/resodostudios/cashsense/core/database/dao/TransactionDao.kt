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
    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getTransactionEntity(id: String): Flow<TransactionEntity>

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getTransactionEntities(): Flow<List<TransactionEntity>>

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getTransactionWithCategoryEntities(): List<TransactionWithCategoryEntity>

    @Upsert
    suspend fun upsertTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE wallet_owner_id = :walletId")
    suspend fun deleteTransactions(walletId: String)

    @Upsert
    suspend fun upsertTransactionCategoryCrossRef(crossRef: TransactionCategoryCrossRefEntity)

    @Query("DELETE FROM transactions_categories WHERE transaction_id = :transactionId")
    suspend fun deleteTransactionCategoryCrossRef(transactionId: String)
}