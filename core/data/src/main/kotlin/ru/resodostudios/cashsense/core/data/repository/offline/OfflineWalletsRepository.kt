package ru.resodostudios.cashsense.core.data.repository.offline

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.resodostudios.cashsense.core.data.model.asEntity
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.database.dao.WalletDao
import ru.resodostudios.cashsense.core.database.model.WalletWithTransactionsEntity
import ru.resodostudios.cashsense.core.database.model.asExternalModel
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.model.data.WalletWithTransactions
import javax.inject.Inject

class OfflineWalletsRepository @Inject constructor(
    private val walletDao: WalletDao
) : WalletsRepository {

    override fun getWalletWithTransactions(walletId: String): Flow<WalletWithTransactions> =
        walletDao.getWalletWithTransactionsEntity(walletId).map { it.asExternalModel() }

    override fun getWalletsWithTransactions(): Flow<List<WalletWithTransactions>> =
        walletDao.getWalletsWithTransactionsEntities().map { it.map(WalletWithTransactionsEntity::asExternalModel) }

    override suspend fun upsertWallet(wallet: Wallet) =
        walletDao.upsertWallet(wallet.asEntity())

    override suspend fun deleteWalletWithTransactions(wallet: Wallet, transactions: List<Transaction>) =
        walletDao.deleteWalletWithTransactions(wallet.asEntity(), transactions.map { it.asEntity() })
}