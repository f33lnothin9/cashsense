package ru.resodostudios.cashsense.feature.home.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.model.data.Transaction

interface TransactionsRepository {

    fun getTransaction(transactionId: String): Flow<Transaction>

    fun getTransactions(): Flow<List<Transaction>>

    suspend fun upsertTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)
}