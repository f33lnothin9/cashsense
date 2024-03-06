package ru.resodostudios.cashsense.feature.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.toInstant
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.Currency
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionCategoryCrossRef
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
) : ViewModel() {

    private val _transactionUiState = MutableStateFlow(TransactionUiState())
    val transactionUiState = _transactionUiState.asStateFlow()

    fun onTransactionEvent(event: TransactionEvent) {
        when (event) {
            TransactionEvent.Save -> saveTransaction()

            TransactionEvent.Delete -> {
                viewModelScope.launch {
                    transactionsRepository.deleteTransaction(_transactionUiState.value.transactionId)
                }
            }

            is TransactionEvent.UpdateId -> {
                _transactionUiState.update {
                    it.copy(transactionId = event.id)
                }
                if (_transactionUiState.value.transactionId.isEmpty()) {
                    _transactionUiState.update {
                        it.copy(
                            transactionId = "",
                            isEditing = false,
                            description = "",
                            amount = "",
                            category = Category(),
                            date = "",
                        )
                    }
                } else {
                    loadTransaction()
                }
            }

            is TransactionEvent.UpdateWalletId -> {
                _transactionUiState.update {
                    it.copy(walletOwnerId = event.id)
                }
            }

            is TransactionEvent.UpdateCurrency -> {
                _transactionUiState.update {
                    it.copy(currency = event.currency)
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

    private fun saveTransaction() {
        val transaction = Transaction(
            id = _transactionUiState.value.transactionId.ifEmpty { UUID.randomUUID().toString() },
            walletOwnerId = _transactionUiState.value.walletOwnerId,
            description = _transactionUiState.value.description,
            amount = _transactionUiState.value.amount.toBigDecimal(),
            date = if (_transactionUiState.value.date.isBlank()) {
                Clock.System.now()
            } else {
                _transactionUiState.value.date.toInstant()
            },
        )
        viewModelScope.launch {
            transactionsRepository.upsertTransaction(transaction)
        }
        viewModelScope.launch {
            transactionsRepository.deleteTransactionCategoryCrossRef(transaction.id)
        }
        viewModelScope.launch {
            val transactionCategoryCrossRef = _transactionUiState.value.category?.id?.let {
                TransactionCategoryCrossRef(
                    transactionId = transaction.id,
                    categoryId = it,
                )
            }
            if (transactionCategoryCrossRef != null) {
                transactionsRepository.upsertTransactionCategoryCrossRef(
                    transactionCategoryCrossRef
                )
            }
        }
        _transactionUiState.value = TransactionUiState()
    }

    private fun loadTransaction() {
        viewModelScope.launch {
            transactionsRepository.getTransactionWithCategory(_transactionUiState.value.transactionId)
                .onStart {
                    _transactionUiState.value = _transactionUiState.value.copy(isEditing = true)
                }
                .catch {
                    _transactionUiState.value = _transactionUiState.value.copy(
                        transactionId = "",
                        isEditing = false,
                        description = "",
                        amount = "",
                        category = Category(),
                        date = "",
                    )
                }
                .collect {
                    _transactionUiState.value = _transactionUiState.value.copy(
                        transactionId = it.transaction.id,
                        description = it.transaction.description.toString(),
                        amount = it.transaction.amount.toString(),
                        date = it.transaction.date.toString(),
                        category = it.category,
                        isEditing = true,
                    )
                }
        }
    }
}

data class TransactionUiState(
    val transactionId: String = "",
    val walletOwnerId: String = "",
    val description: String = "",
    val amount: String = "",
    val currency: String = Currency.USD.name,
    val date: String = "",
    val category: Category? = Category(),
    val isEditing: Boolean = false,
)