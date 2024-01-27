package ru.resodostudios.cashsense.feature.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionCategoryCrossRef
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository
) : ViewModel() {

    fun upsertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionsRepository.upsertTransaction(transaction)
            if (transaction.categoryId != null) {
                transactionsRepository.deleteTransactionCategoryCrossRef(transaction.id)
                transactionsRepository.upsertTransactionCategoryCrossRef(
                    TransactionCategoryCrossRef(
                        transactionId = transaction.id,
                        categoryId = transaction.categoryId!!
                    )
                )
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionsRepository.deleteTransaction(transaction)
            transactionsRepository.deleteTransactionCategoryCrossRef(transaction.id)
        }
    }
}