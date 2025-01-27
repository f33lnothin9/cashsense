package ru.resodostudios.cashsense.core.data.repository.offline

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.resodostudios.cashsense.core.data.model.asEntity
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.database.dao.TransactionDao
import ru.resodostudios.cashsense.core.database.model.TransactionCategoryCrossRefEntity
import ru.resodostudios.cashsense.core.database.model.asExternalModel
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionCategoryCrossRef
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import javax.inject.Inject
import kotlin.uuid.Uuid

internal class OfflineTransactionRepository @Inject constructor(
    private val dao: TransactionDao,
) : TransactionsRepository {

    override fun getTransactionWithCategory(transactionId: String): Flow<TransactionWithCategory> =
        dao.getTransactionWithCategoryEntity(transactionId).map { it.asExternalModel() }

    override fun getTransactionCategoryCrossRefs(categoryId: String): Flow<List<TransactionCategoryCrossRef>> =
        dao.getTransactionCategoryCrossRefs(categoryId).map {
            it.map(TransactionCategoryCrossRefEntity::asExternalModel)
        }

    override suspend fun upsertTransaction(transaction: Transaction) =
        dao.upsertTransaction(transaction.asEntity())

    override suspend fun deleteTransaction(id: String) =
        dao.deleteTransaction(id)

    override suspend fun upsertTransactionCategoryCrossRef(crossRef: TransactionCategoryCrossRef) =
        dao.upsertTransactionCategoryCrossRef(crossRef.asEntity())

    override suspend fun deleteTransactionCategoryCrossRef(transactionId: String) =
        dao.deleteTransactionCategoryCrossRef(transactionId)

    override suspend fun deleteTransfer(uuid: Uuid) =
        dao.deleteTransfer(uuid)
}