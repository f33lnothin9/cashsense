package ru.resodostudios.cashsense.core.data.repository

import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.model.data.Wallet

interface WalletsRepository {

    fun getWallet(walletId: String): Flow<Wallet>

    fun getWallets(): Flow<List<Wallet>>

    suspend fun upsertWallet(wallet: Wallet)

    suspend fun deleteWallet(wallet: Wallet)
}