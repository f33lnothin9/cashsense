package ru.resodostudios.cashsense.core.data.repository.offline

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.resodostudios.cashsense.core.data.model.asEntity
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.database.dao.WalletDao
import ru.resodostudios.cashsense.core.database.model.WalletWithTransactionsAndCategoriesEntity
import ru.resodostudios.cashsense.core.database.model.asExternalModel
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.model.data.WalletWithTransactionsAndCategories
import javax.inject.Inject

class OfflineWalletsRepository @Inject constructor(
    private val dao: WalletDao
) : WalletsRepository {
    override fun getWalletWithTransactions(walletId: Long): Flow<WalletWithTransactionsAndCategories> =
        dao.getWalletWithTransactionsEntity(walletId).map { it.asExternalModel() }

    override fun getWalletsWithTransactionsAndCategories(): Flow<List<WalletWithTransactionsAndCategories>> =
        dao.getWalletWithTransactionsAndCategoriesEntities().map { it.map(WalletWithTransactionsAndCategoriesEntity::asExternalModel) }

    override suspend fun upsertWallet(wallet: Wallet) =
        dao.upsertWallet(wallet.asEntity())

    override suspend fun deleteWalletWithTransactions(wallet: Wallet, transactions: List<Transaction>) =
        dao.deleteWalletWithTransactions(wallet.asEntity(), transactions.map { it.asEntity() })
}