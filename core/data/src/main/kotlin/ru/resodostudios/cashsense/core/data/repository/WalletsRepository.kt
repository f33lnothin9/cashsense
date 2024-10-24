package ru.resodostudios.cashsense.core.data.repository

import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.model.data.WalletExtended

interface WalletsRepository {

    fun getWallet(id: String): Flow<Wallet>

    fun getWalletWithTransactionsAndCategories(walletId: String): Flow<WalletExtended>

    fun getWalletsWithTransactionsAndCategories(): Flow<List<WalletExtended>>

    suspend fun upsertWallet(wallet: Wallet)

    suspend fun deleteWalletWithTransactions(id: String)
}