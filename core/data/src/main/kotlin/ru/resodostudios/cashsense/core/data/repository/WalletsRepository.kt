package ru.resodostudios.cashsense.core.data.repository

import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.model.data.WalletWithTransactionsAndCategories

interface WalletsRepository {

    fun getWalletWithTransactions(walletId: Long): Flow<WalletWithTransactionsAndCategories>

    fun getWalletsWithTransactionsAndCategories(): Flow<List<WalletWithTransactionsAndCategories>>

    suspend fun upsertWallet(wallet: Wallet)

    suspend fun deleteWalletWithTransactions(wallet: Wallet, transactions: List<Transaction>)
}