package ru.resodostudios.cashsense.feature.transaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.toInstant
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionCategoryCrossRef
import ru.resodostudios.cashsense.feature.transaction.navigation.TransactionArgs
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val transactionsRepository: TransactionsRepository,
) : ViewModel() {

    private val transactionArgs: TransactionArgs = TransactionArgs(savedStateHandle)
    private val transactionId = transactionArgs.transactionId
    private val walletId = transactionArgs.walletId

    private val _transactionUiState = MutableStateFlow(TransactionUiState())
    val transactionUiState = _transactionUiState.asStateFlow()

    init {
        loadTransaction()
    }

    fun onTransactionEvent(event: TransactionEvent) {
        when (event) {
            TransactionEvent.Confirm -> {
                val transaction = Transaction(
                    id = transactionId ?: UUID.randomUUID().toString(),
                    walletOwnerId = walletId,
                    description = _transactionUiState.value.description,
                    amount = _transactionUiState.value.amount.toBigDecimal(),
                    date = _transactionUiState.value.date.toInstant(),
                )
                viewModelScope.launch {
                    transactionsRepository.upsertTransaction(transaction)
                }
                viewModelScope.launch {
                    transactionsRepository.deleteTransactionCategoryCrossRef(transaction.id)
                }
                viewModelScope.launch {
                    transactionsRepository.upsertTransactionCategoryCrossRef(
                        TransactionCategoryCrossRef(
                            transactionId = transaction.id,
                            categoryId = _transactionUiState.value.category?.id.toString()
                        )
                    )
                }
            }

            is TransactionEvent.UpdateAmount -> {
                _transactionUiState.update {
                    it.copy(amount = event.amount)
                }
            }

            is TransactionEvent.UpdateCategory -> {
                _transactionUiState.update {
                    it.copy(category = event.category)
                }
            }

            is TransactionEvent.UpdateDescription -> {
                _transactionUiState.update {
                    it.copy(description = event.description)
                }
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionsRepository.deleteTransaction(transaction)
        }
    }

    private fun loadTransaction() {
        viewModelScope.launch {
            if (transactionId != null) {
                transactionsRepository.getTransactionWithCategory(transactionId)
                    .onEach {
                        _transactionUiState.emit(
                            TransactionUiState(
                                walletOwnerId = walletId,
                                description = it.transaction.description.toString(),
                                amount = it.transaction.amount.toString(),
                                date = it.transaction.date.toString(),
                                category = it.category,
                                isEditing = true,
                            )
                        )
                    }
                    .collect()
            }
        }
    }
}