package ru.resodostudios.cashsense.core.data.repository.offline

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.resodostudios.cashsense.core.data.model.asEntity
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.database.dao.TransactionDao
import ru.resodostudios.cashsense.core.database.dao.WalletDao
import ru.resodostudios.cashsense.core.database.model.PopulatedWallet
import ru.resodostudios.cashsense.core.database.model.asExternalModel
import ru.resodostudios.cashsense.core.datastore.CsPreferencesDataSource
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.model.data.WalletExtended
import javax.inject.Inject

internal class OfflineWalletsRepository @Inject constructor(
    private val walletDao: WalletDao,
    private val transactionDao: TransactionDao,
    private val csPreferencesDataSource: CsPreferencesDataSource,
) : WalletsRepository {

    override fun getWallet(id: String): Flow<Wallet> =
        walletDao.getWalletEntity(id).map { it.asExternalModel() }

    override fun getWalletWithTransactionsAndCategories(walletId: String): Flow<WalletExtended> =
        walletDao.getWalletWithTransactionsAndCategoriesEntity(walletId).map { it.asExternalModel() }

    override fun getWalletsWithTransactionsAndCategories(): Flow<List<WalletExtended>> =
        walletDao.getWalletWithTransactionsAndCategoriesEntities().map { it.map(PopulatedWallet::asExternalModel) }

    override suspend fun upsertWallet(wallet: Wallet) =
        walletDao.upsertWallet(wallet.asEntity())

    override suspend fun deleteWalletWithTransactions(id: String) {
        walletDao.deleteWallet(id)
        transactionDao.deleteTransactions(id)
        val userData = csPreferencesDataSource.userData.first()
        if (id == userData.primaryWalletId) {
            csPreferencesDataSource.setPrimaryWalletId("")
        }
    }
}