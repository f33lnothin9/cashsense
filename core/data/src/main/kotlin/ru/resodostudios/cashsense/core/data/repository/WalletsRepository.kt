package ru.resodostudios.cashsense.core.data.repository

import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.model.data.ExtendedWallet

interface WalletsRepository {

    fun getWallet(id: String): Flow<Wallet>

    fun getWalletWithTransactionsAndCategories(walletId: String): Flow<ExtendedWallet>

    fun getWalletsWithTransactionsAndCategories(): Flow<List<ExtendedWallet>>

    suspend fun upsertWallet(wallet: Wallet)

    suspend fun deleteWalletWithTransactions(id: String)
}