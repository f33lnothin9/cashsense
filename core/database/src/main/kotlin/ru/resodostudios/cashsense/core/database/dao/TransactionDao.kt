package ru.resodostudios.cashsense.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.database.model.TransactionCategoryCrossRefEntity
import ru.resodostudios.cashsense.core.database.model.TransactionEntity
import ru.resodostudios.cashsense.core.database.model.PopulatedTransaction

@Dao
interface TransactionDao {

    @Transaction
    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    fun getTransactionWithCategoryEntity(transactionId: String): Flow<PopulatedTransaction>

    @Upsert
    suspend fun upsertTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: String)

    @Query("DELETE FROM transactions WHERE wallet_owner_id = :walletId")
    suspend fun deleteTransactions(walletId: String)

    @Upsert
    suspend fun upsertTransactionCategoryCrossRef(crossRef: TransactionCategoryCrossRefEntity)

    @Query("DELETE FROM transactions_categories WHERE transaction_id = :transactionId")
    suspend fun deleteTransactionCategoryCrossRef(transactionId: String)
}