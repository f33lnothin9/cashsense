package ru.resodostudios.cashsense.core.data.repository

import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionCategoryCrossRef

interface TransactionsRepository {
    fun getTransaction(id: String): Flow<Transaction>

    suspend fun upsertTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)

    suspend fun upsertTransactionCategoryCrossRef(crossRef: TransactionCategoryCrossRef)
}