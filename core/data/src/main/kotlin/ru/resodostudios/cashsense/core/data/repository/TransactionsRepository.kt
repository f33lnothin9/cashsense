package ru.resodostudios.cashsense.core.data.repository

import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionCategoryCrossRef
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory

interface TransactionsRepository {

    fun getTransactionWithCategory(transactionId: String): Flow<TransactionWithCategory>

    suspend fun upsertTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)

    suspend fun upsertTransactionCategoryCrossRef(crossRef: TransactionCategoryCrossRef)

    suspend fun deleteTransactionCategoryCrossRef(transactionId: String)
}