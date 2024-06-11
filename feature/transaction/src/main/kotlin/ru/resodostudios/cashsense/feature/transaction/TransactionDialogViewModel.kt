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
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.Delete
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.Save
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateAmount
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateCategory
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateCurrency
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateDate
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateDescription
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateTransactionId
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateTransactionType
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateWalletId
import ru.resodostudios.cashsense.feature.transaction.TransactionType.EXPENSE
import ru.resodostudios.cashsense.feature.transaction.TransactionType.INCOME
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
            Save -> saveTransaction()
            Delete -> deleteTransaction()
            is UpdateTransactionId -> updateTransactionId(event.id)
            is UpdateWalletId -> updateWalletId(event.id)
            is UpdateCurrency -> updateCurrency(event.currency)
            is UpdateDate -> updateDate(event.date)
            is UpdateAmount -> updateAmount(event.amount)
            is UpdateTransactionType -> updateTransactionType(event.type)
            is UpdateCategory -> updateCategory(event.category)
            is UpdateDescription -> updateDescription(event.description)
        }
    }

    private fun saveTransaction() {
        val transaction = Transaction(
            id = _transactionDialogUiState.value.transactionId.ifEmpty {
                UUID.randomUUID().toString()
            },
            walletOwnerId = walletId.value,
            description = _transactionDialogUiState.value.description,
            amount = if (_transactionDialogUiState.value.transactionType == EXPENSE) {
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

    private fun deleteTransaction() {
        viewModelScope.launch {
            transactionsRepository.deleteTransaction(_transactionDialogUiState.value.transactionId)
        }
    }

    private fun updateTransactionId(id: String) {
        _transactionDialogUiState.update {
            it.copy(transactionId = id)
        }
        if (_transactionDialogUiState.value.transactionId.isEmpty()) {
            _transactionDialogUiState.update {
                TransactionDialogUiState()
            }
        } else {
            loadTransaction()
        }
    }

    private fun updateWalletId(id: String) {
        savedStateHandle[WALLET_ID] = id
    }

    private fun updateCurrency(currency: String) {
        _transactionDialogUiState.update {
            it.copy(currency = currency)
        }
    }

    private fun updateDate(date: Instant) {
        _transactionDialogUiState.update {
            it.copy(date = date)
        }
    }

    private fun updateAmount(amount: String) {
        _transactionDialogUiState.update {
            it.copy(amount = amount)
        }
    }

    private fun updateTransactionType(type: TransactionType) {
        _transactionDialogUiState.update {
            it.copy(transactionType = type)
        }
    }

    private fun updateCategory(category: Category) {
        _transactionDialogUiState.update {
            it.copy(category = category)
        }
    }

    private fun updateDescription(description: String) {
        _transactionDialogUiState.update {
            it.copy(description = description)
        }
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
                        transactionType = if (it.transaction.amount < BigDecimal.ZERO) EXPENSE else INCOME,
                        date = it.transaction.timestamp,
                        category = it.category,
                    )
                }
        }
    }
}

enum class TransactionType {
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
    val transactionType: TransactionType = EXPENSE,
    val isLoading: Boolean = false,
)

private const val WALLET_ID = "walletId"