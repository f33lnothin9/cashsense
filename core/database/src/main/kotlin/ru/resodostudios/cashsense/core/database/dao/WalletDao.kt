package ru.resodostudios.cashsense.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.database.model.TransactionEntity
import ru.resodostudios.cashsense.core.database.model.WalletEntity
import ru.resodostudios.cashsense.core.database.model.WalletWithTransactionsEntity

@Dao
interface WalletDao {

    @Transaction
    @Query("SELECT * FROM wallets WHERE walletId = :walletId")
    fun getWalletWithTransactionsEntity(walletId: Long): Flow<WalletWithTransactionsEntity>

    @Transaction
    @Query("SELECT * FROM wallets ORDER BY walletId DESC")
    fun getWalletsWithTransactionsEntities(): Flow<List<WalletWithTransactionsEntity>>

    @Upsert
    suspend fun upsertWallet(wallet: WalletEntity)

    @Delete
    suspend fun deleteWalletWithTransactions(wallet: WalletEntity, transactions: List<TransactionEntity>)
}