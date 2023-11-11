package ru.resodostudios.cashsense.core.data.repository.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.resodostudios.cashsense.core.data.model.asEntity
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.database.dao.TransactionDao
import ru.resodostudios.cashsense.core.database.model.TransactionEntity
import ru.resodostudios.cashsense.core.database.model.asExternalModel
import ru.resodostudios.cashsense.core.model.data.Transaction
import javax.inject.Inject

class TransactionsRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionsRepository {

    override fun getTransaction(transactionId: String): Flow<Transaction> =
        dao.getTransactionEntity(transactionId).map { it.asExternalModel() }

    override fun getTransactions(): Flow<List<Transaction>> =
        dao.getTransactionEntities().map { it.map(TransactionEntity::asExternalModel) }

    override suspend fun upsertTransaction(transaction: Transaction) =
        dao.upsertTransaction(transaction.asEntity())

    override suspend fun deleteTransaction(transaction: Transaction) =
        dao.deleteTransaction(transaction.asEntity())
}