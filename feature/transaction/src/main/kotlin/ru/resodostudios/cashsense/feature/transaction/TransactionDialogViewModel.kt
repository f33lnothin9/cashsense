package ru.resodostudios.cashsense.feature.transaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import ru.resodostudios.cashsense.core.data.repository.CategoriesRepository
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.StatusType
import ru.resodostudios.cashsense.core.model.data.StatusType.COMPLETED
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionCategoryCrossRef
import ru.resodostudios.cashsense.core.ui.CategoriesUiState
import ru.resodostudios.cashsense.core.ui.CategoriesUiState.Loading
import ru.resodostudios.cashsense.core.ui.CategoriesUiState.Success
import ru.resodostudios.cashsense.core.util.Constants.WALLET_ID_KEY
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.Repeat
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.Save
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateAmount
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateCategory
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateCurrency
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateDate
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateDescription
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateStatus
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateTransactionId
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateTransactionIgnoring
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateTransactionType
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateWalletId
import ru.resodostudios.cashsense.feature.transaction.TransactionType.EXPENSE
import ru.resodostudios.cashsense.feature.transaction.TransactionType.INCOME
import ru.resodostudios.cashsense.feature.transaction.navigation.TransactionDialogRoute
import java.math.BigDecimal.ZERO
import java.util.Currency
import javax.inject.Inject
import kotlin.uuid.Uuid

@HiltViewModel
class TransactionDialogViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val transactionsRepository: TransactionsRepository,
    categoriesRepository: CategoriesRepository,
) : ViewModel() {

    private val transactionDestination: TransactionDialogRoute = savedStateHandle.toRoute()

    private val _transactionDialogUiState = MutableStateFlow(TransactionDialogUiState())
    val transactionDialogUiState: StateFlow<TransactionDialogUiState>
        get() = _transactionDialogUiState.asStateFlow()

    val categoriesUiState: StateFlow<CategoriesUiState> =
        categoriesRepository.getCategories()
            .map { Success(false, it, null) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = Loading,
            )

    init {
        updateWalletId(transactionDestination.walletId)
        transactionDestination.transactionId?.let(::loadTransaction)
    }

    fun onTransactionEvent(event: TransactionDialogEvent) {
        when (event) {
            Save -> saveTransaction()
            Repeat -> repeatTransaction()
            is UpdateTransactionId -> updateTransactionId(event.id)
            is UpdateWalletId -> updateWalletId(event.id)
            is UpdateCurrency -> updateCurrency(event.currency)
            is UpdateDate -> updateDate(event.date)
            is UpdateAmount -> updateAmount(event.amount)
            is UpdateTransactionType -> updateTransactionType(event.type)
            is UpdateStatus -> updateStatus(event.status)
            is UpdateCategory -> updateCategory(event.category)
            is UpdateDescription -> updateDescription(event.description)
            is UpdateTransactionIgnoring -> updateTransactionIgnoring(event.ignored)
        }
    }

    private fun saveTransaction() {
        val transaction = Transaction(
            id = _transactionDialogUiState.value.transactionId.ifBlank { Uuid.random().toHexString() },
            walletOwnerId = transactionDestination.walletId,
            description = _transactionDialogUiState.value.description.ifBlank { null },
            amount = _transactionDialogUiState.value.amount
                .toBigDecimal()
                .run { if (_transactionDialogUiState.value.transactionType == EXPENSE) negate() else abs() },
            timestamp = _transactionDialogUiState.value.date,
            status = _transactionDialogUiState.value.status,
            ignored = _transactionDialogUiState.value.ignored,
            transferId = null,
        )
        val transactionCategoryCrossRef =
            _transactionDialogUiState.value.category?.id?.let { categoryId ->
                TransactionCategoryCrossRef(
                    transactionId = transaction.id,
                    categoryId = categoryId,
                )
            }
        viewModelScope.launch {
            transactionsRepository.upsertTransaction(transaction)
            transactionsRepository.deleteTransactionCategoryCrossRef(transaction.id)
            if (transactionCategoryCrossRef != null) {
                transactionsRepository.upsertTransactionCategoryCrossRef(transactionCategoryCrossRef)
            }
            _transactionDialogUiState.update { it.copy(isTransactionSaved = true) }
        }
    }

    private fun updateTransactionId(id: String) {
        _transactionDialogUiState.update {
            it.copy(transactionId = id)
        }
    }

    private fun updateWalletId(id: String) {
        savedStateHandle[WALLET_ID_KEY] = id
    }

    private fun updateCurrency(currency: Currency) {
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

    private fun updateStatus(status: StatusType) {
        _transactionDialogUiState.update {
            it.copy(status = status)
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

    private fun updateTransactionIgnoring(ignored: Boolean) {
        _transactionDialogUiState.update {
            it.copy(ignored = ignored)
        }
    }

    private fun repeatTransaction() {
        _transactionDialogUiState.update {
            it.copy(
                transactionId = "",
                date = Clock.System.now(),
            )
        }
    }

    private fun loadTransaction(id: String) {
        viewModelScope.launch {
            _transactionDialogUiState.update {
                TransactionDialogUiState(
                    transactionId = if (transactionDestination.repeated) "" else id,
                    isLoading = true,
                )
            }
            val transactionCategory = transactionsRepository.getTransactionWithCategory(id).first()
            val date = if (transactionDestination.repeated) {
                Clock.System.now()
            } else {
                transactionCategory.transaction.timestamp
            }
            _transactionDialogUiState.update {
                it.copy(
                    description = transactionCategory.transaction.description ?: "",
                    amount = transactionCategory.transaction.amount.abs().toString(),
                    transactionType = if (transactionCategory.transaction.amount < ZERO) EXPENSE else INCOME,
                    date = date,
                    category = transactionCategory.category,
                    status = transactionCategory.transaction.status,
                    ignored = transactionCategory.transaction.ignored,
                    isLoading = false,
                    isTransfer = transactionCategory.transaction.transferId != null,
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
    val currency: Currency = Currency.getInstance("USD"),
    val date: Instant = Clock.System.now(),
    val category: Category? = Category(),
    val transactionType: TransactionType = EXPENSE,
    val status: StatusType = COMPLETED,
    val ignored: Boolean = false,
    val isLoading: Boolean = false,
    val isTransfer: Boolean = false,
    val isTransactionSaved: Boolean = false,
)