package ru.resodostudios.cashsense.core.data.repository

import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.model.data.Transaction

interface TransactionRepository {

    fun getTransaction(transactionId: String): Flow<Transaction>

    suspend fun upsertTransaction(transaction: Transaction)

    suspend fun deleteTransaction(transaction: Transaction)
}