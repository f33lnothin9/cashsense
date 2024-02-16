package ru.resodostudios.cashsense.feature.transaction

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
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
import kotlinx.datetime.Clock
import kotlinx.datetime.toInstant
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.model.data.Category
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
                    description = _transactionUiState.value.description.text,
                    amount = _transactionUiState.value.amount.text.toBigDecimal(),
                    date = _transactionUiState.value.date.toInstant(),
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
                            categoryId = it
                        )
                    }
                    if (transactionCategoryCrossRef != null) {
                        transactionsRepository.upsertTransactionCategoryCrossRef(
                            transactionCategoryCrossRef
                        )
                    }
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
                                description = TextFieldValue(
                                    text = it.transaction.description ?: "",
                                    selection = TextRange(it.transaction.description?.length ?: 0)
                                ),
                                amount = TextFieldValue(
                                    text = it.transaction.amount.toString(),
                                    selection = TextRange(it.transaction.amount.toString().length)
                                ),
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

data class TransactionUiState(
    val walletOwnerId: String = "",
    val description: TextFieldValue = TextFieldValue(""),
    val amount: TextFieldValue = TextFieldValue(""),
    val date: String = Clock.System.now().toString(),
    val category: Category? = Category(),
    val isEditing: Boolean = false,
)