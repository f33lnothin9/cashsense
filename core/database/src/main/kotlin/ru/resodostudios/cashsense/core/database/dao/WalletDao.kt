package ru.resodostudios.cashsense.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.database.model.WalletEntity
import ru.resodostudios.cashsense.core.database.model.WalletWithTransactionsAndCategoriesEntity

@Dao
interface WalletDao {

    @Query("SELECT * FROM wallets WHERE id = :id")
    fun getWalletEntity(id: String): Flow<WalletEntity>

    @Transaction
    @Query("SELECT * FROM wallets WHERE id = :walletId")
    fun getWalletWithTransactionsAndCategoriesEntity(walletId: String): Flow<WalletWithTransactionsAndCategoriesEntity>

    @Transaction
    @Query("SELECT * FROM wallets")
    fun getWalletWithTransactionsAndCategoriesEntities(): Flow<List<WalletWithTransactionsAndCategoriesEntity>>

    @Upsert
    suspend fun upsertWallet(wallet: WalletEntity)

    @Query("DELETE FROM wallets WHERE id = :id")
    suspend fun deleteWallet(id: String)
}