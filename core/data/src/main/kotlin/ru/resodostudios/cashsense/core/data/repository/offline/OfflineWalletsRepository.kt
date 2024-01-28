package ru.resodostudios.cashsense.core.data.repository.offline

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.resodostudios.cashsense.core.data.model.asEntity
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.database.dao.TransactionDao
import ru.resodostudios.cashsense.core.database.dao.WalletDao
import ru.resodostudios.cashsense.core.database.model.WalletWithTransactionsAndCategoriesEntity
import ru.resodostudios.cashsense.core.database.model.asExternalModel
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.model.data.WalletWithTransactionsAndCategories
import javax.inject.Inject

class OfflineWalletsRepository @Inject constructor(
    private val walletDao: WalletDao,
    private val transactionDao: TransactionDao
) : WalletsRepository {
    override fun getWalletWithTransactions(id: String): Flow<WalletWithTransactionsAndCategories> =
        walletDao.getWalletWithTransactionsEntity(id).map { it.asExternalModel() }

    override fun getWalletsWithTransactions(): Flow<List<WalletWithTransactionsAndCategories>> =
        walletDao.getWalletWithTransactionsEntities().map { it.map(WalletWithTransactionsAndCategoriesEntity::asExternalModel) }

    override suspend fun upsertWallet(wallet: Wallet) =
        walletDao.upsertWallet(wallet.asEntity())

    override suspend fun deleteWallet(id: String) {
        walletDao.deleteWallet(id)
        transactionDao.deleteTransactions(id)
    }
}