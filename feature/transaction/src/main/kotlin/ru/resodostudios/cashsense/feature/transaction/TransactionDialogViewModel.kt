package ru.resodostudios.cashsense.feature.transaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionCategoryCrossRef
import ru.resodostudios.cashsense.feature.transaction.FinancialType.EXPENSE
import ru.resodostudios.cashsense.feature.transaction.FinancialType.INCOME
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TransactionDialogViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val walletId = savedStateHandle.getStateFlow(key = WALLET_ID, initialValue = "")

    private val _transactionDialogUiState = MutableStateFlow(TransactionDialogUiState())
    val transactionDialogUiState = _transactionDialogUiState.asStateFlow()

    fun onTransactionEvent(event: TransactionDialogEvent) {
        when (event) {
            TransactionDialogEvent.Save -> saveTransaction()

            TransactionDialogEvent.Delete -> {
                viewModelScope.launch {
                    transactionsRepository.deleteTransaction(_transactionDialogUiState.value.transactionId)
                }
            }

            is TransactionDialogEvent.UpdateId -> {
                _transactionDialogUiState.update {
                    it.copy(transactionId = event.id)
                }
                if (_transactionDialogUiState.value.transactionId.isEmpty()) {
                    _transactionDialogUiState.update {
                        TransactionDialogUiState()
                    }
                } else {
                    loadTransaction()
                }
            }

            is TransactionDialogEvent.UpdateWalletId -> {
                savedStateHandle[WALLET_ID] = event.id
            }

            is TransactionDialogEvent.UpdateCurrency -> {
                _transactionDialogUiState.update {
                    it.copy(currency = event.currency)
                }
            }

            is TransactionDialogEvent.UpdateDate -> {
                _transactionDialogUiState.update {
                    it.copy(date = event.date)
                }
            }

            is TransactionDialogEvent.UpdateAmount -> {
                _transactionDialogUiState.update {
                    it.copy(amount = event.amount)
                }
            }

            is TransactionDialogEvent.UpdateFinancialType -> {
                _transactionDialogUiState.update {
                    it.copy(financialType = event.type)
                }
            }

            is TransactionDialogEvent.UpdateCategory -> {
                _transactionDialogUiState.update {
                    it.copy(category = event.category)
                }
            }

            is TransactionDialogEvent.UpdateDescription -> {
                _transactionDialogUiState.update {
                    it.copy(description = event.description)
                }
            }
        }
    }

    private fun saveTransaction() {
        val transaction = Transaction(
            id = _transactionDialogUiState.value.transactionId.ifEmpty { UUID.randomUUID().toString() },
            walletOwnerId = walletId.value,
            description = _transactionDialogUiState.value.description,
            amount = if (_transactionDialogUiState.value.financialType == EXPENSE) {
                _transactionDialogUiState.value.amount.toBigDecimal().run {
                    if (this > BigDecimal.ZERO) this.negate() else this
                }
            } else {
                _transactionDialogUiState.value.amount.toBigDecimal().abs()
            },
            timestamp = _transactionDialogUiState.value.date,
        )
        val transactionCategoryCrossRef = _transactionDialogUiState.value.category?.id?.let {
            TransactionCategoryCrossRef(
                transactionId = transaction.id,
                categoryId = it,
            )
        }
        viewModelScope.launch {
            launch {
                transactionsRepository.upsertTransaction(transaction)
            }.join()
            launch {
                transactionsRepository.deleteTransactionCategoryCrossRef(transaction.id)
            }.join()
            if (transactionCategoryCrossRef != null) {
                launch {
                    transactionsRepository.upsertTransactionCategoryCrossRef(
                        transactionCategoryCrossRef
                    )
                }
            }
        }
        _transactionDialogUiState.value = TransactionDialogUiState()
    }

    private fun loadTransaction() {
        viewModelScope.launch {
            transactionsRepository.getTransactionWithCategory(_transactionDialogUiState.value.transactionId)
                .onStart { _transactionDialogUiState.update { it.copy(isLoading = true) } }
                .onCompletion { _transactionDialogUiState.update { it.copy(isLoading = false) } }
                .catch { _transactionDialogUiState.value = TransactionDialogUiState() }
                .collect {
                    _transactionDialogUiState.value = TransactionDialogUiState(
                        transactionId = it.transaction.id,
                        description = it.transaction.description.toString(),
                        amount = it.transaction.amount.toString(),
                        financialType = if (it.transaction.amount < BigDecimal.ZERO) EXPENSE else INCOME,
                        date = it.transaction.timestamp,
                        category = it.category,
                    )
                }
        }
    }
}

enum class FinancialType {
    EXPENSE,
    INCOME,
}

data class TransactionDialogUiState(
    val transactionId: String = "",
    val description: String = "",
    val amount: String = "",
    val currency: String = "USD",
    val date: Instant = Clock.System.now(),
    val category: Category? = Category(),
    val financialType: FinancialType = EXPENSE,
    val isLoading: Boolean = false,
)

private const val WALLET_ID = "walletId"