package ru.resodostudios.cashsense.feature.wallet.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.ui.getCurrentZonedDateTime
import ru.resodostudios.cashsense.core.ui.getZonedDateTime
import ru.resodostudios.cashsense.core.ui.isInCurrentMonthAndYear
import ru.resodostudios.cashsense.feature.wallet.detail.DateType.ALL
import ru.resodostudios.cashsense.feature.wallet.detail.DateType.MONTH
import ru.resodostudios.cashsense.feature.wallet.detail.DateType.WEEK
import ru.resodostudios.cashsense.feature.wallet.detail.DateType.YEAR
import ru.resodostudios.cashsense.feature.wallet.detail.FinanceType.EXPENSES
import ru.resodostudios.cashsense.feature.wallet.detail.FinanceType.INCOME
import ru.resodostudios.cashsense.feature.wallet.detail.FinanceType.NONE
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.AddToSelectedCategories
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.ClearUndoState
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.DecrementSelectedDate
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.HideTransaction
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.IncrementSelectedDate
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.RemoveFromSelectedCategories
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.UndoTransactionRemoval
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.UpdateDateType
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.UpdateFinanceType
import ru.resodostudios.cashsense.feature.wallet.detail.WalletUiState.Loading
import ru.resodostudios.cashsense.feature.wallet.detail.WalletUiState.Success
import ru.resodostudios.cashsense.feature.wallet.detail.navigation.WalletRoute
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.time.temporal.WeekFields
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    walletsRepository: WalletsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val walletRoute: WalletRoute = savedStateHandle.toRoute()

    private val shouldDisplayUndoTransactionState = MutableStateFlow(false)
    private val lastRemovedTransactionIdState = MutableStateFlow<String?>(null)

    private val walletFilterState = MutableStateFlow(
        WalletFilter(
            selectedCategories = emptyList(),
            availableCategories = emptyList(),
            financeType = NONE,
            dateType = ALL,
            availableYears = emptyList(),
            selectedDate = 0,
        )
    )

    val walletUiState: StateFlow<WalletUiState> = combine(
        walletsRepository.getWalletWithTransactions(walletRoute.walletId),
        walletFilterState,
        shouldDisplayUndoTransactionState,
        lastRemovedTransactionIdState,
    ) { walletTransactionsCategories, walletFilter, shouldDisplayUndoTransaction, lastRemovedTransactionId ->
        val currentBalance = walletTransactionsCategories.wallet.initialBalance.plus(
            walletTransactionsCategories.transactionsWithCategories.sumOf { it.transaction.amount }
        )
        val sortedTransactions = walletTransactionsCategories.transactionsWithCategories
            .sortedByDescending { it.transaction.timestamp }
        val financeTypeTransactions = when (walletFilter.financeType) {
            NONE -> sortedTransactions
            EXPENSES -> sortedTransactions.filter { it.transaction.amount < ZERO }
            INCOME -> sortedTransactions.filter { it.transaction.amount > ZERO }
        }
        walletFilterState.update {
            val availableYears = financeTypeTransactions
                .map { it.transaction.timestamp.getZonedDateTime().year }
                .toSortedSet()
                .toList()
            it.copy(availableYears = availableYears)
        }
        val dateTypeTransactions = when (walletFilter.dateType) {
            WEEK -> financeTypeTransactions.filter {
                val weekOfTransaction = it.transaction.timestamp
                    .getZonedDateTime()
                    .get(WeekFields.ISO.weekOfWeekBasedYear())
                weekOfTransaction == getCurrentZonedDateTime().get(WeekFields.ISO.weekOfWeekBasedYear())
            }

            MONTH -> financeTypeTransactions.filter {
                it.transaction.timestamp
                    .getZonedDateTime()
                    .isInCurrentMonthAndYear()
            }

            YEAR -> {
                financeTypeTransactions.filter {
                    it.transaction.timestamp.getZonedDateTime().year == walletFilterState.value.selectedDate
                }
            }

            ALL -> financeTypeTransactions
        }

        val filteredTransactionsCategories = dateTypeTransactions
            .filterNot { it.transaction.id == lastRemovedTransactionId }

        calculateAvailableCategories(filteredTransactionsCategories)
        val filteredByCategories = if (walletFilterState.value.selectedCategories.isNotEmpty()) {
            filteredTransactionsCategories
                .filter { walletFilterState.value.selectedCategories.contains(it.category) }
                .apply {
                    if (this.isEmpty()) {
                        walletFilterState.update {
                            it.copy(selectedCategories = emptyList())
                        }
                    }
                }
        } else filteredTransactionsCategories

        Success(
            currentBalance = currentBalance,
            walletFilter = WalletFilter(
                availableCategories = walletFilter.availableCategories.minus(Category()),
                selectedCategories = walletFilter.selectedCategories,
                financeType = walletFilter.financeType,
                dateType = walletFilter.dateType,
                availableYears = walletFilter.availableYears,
                selectedDate = walletFilter.selectedDate,
            ),
            wallet = walletTransactionsCategories.wallet,
            transactionsCategories = filteredByCategories,
            shouldDisplayUndoTransaction = shouldDisplayUndoTransaction,
        )
    }
        .catch { Loading }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Loading,
        )

    private fun calculateAvailableCategories(transactionsCategories: List<TransactionWithCategory>) {
        transactionsCategories
            .map { it.category }
            .toSet()
            .also { categories ->
                walletFilterState.update {
                    it.copy(availableCategories = categories.filterNotNull())
                }
            }
    }

    fun onWalletEvent(event: WalletEvent) {
        when (event) {
            is AddToSelectedCategories -> addToSelectedCategories(event.category)
            is RemoveFromSelectedCategories -> removeFromSelectedCategories(event.category)
            is UpdateFinanceType -> updateFinanceType(event.financeType)
            is UpdateDateType -> updateDateType(event.dateType)
            is HideTransaction -> hideTransaction(event.id)
            ClearUndoState -> clearUndoState()
            UndoTransactionRemoval -> undoTransactionRemoval()
            DecrementSelectedDate -> decrementSelectedDate()
            IncrementSelectedDate -> incrementSelectedDate()
        }
    }

    private fun deleteTransaction(id: String) {
        viewModelScope.launch {
            transactionsRepository.deleteTransaction(id)
        }
    }

    private fun addToSelectedCategories(category: Category) {
        walletFilterState.update {
            it.copy(selectedCategories = it.selectedCategories + category)
        }
    }

    private fun removeFromSelectedCategories(category: Category) {
        walletFilterState.update {
            it.copy(selectedCategories = it.selectedCategories - category)
        }
    }

    private fun updateFinanceType(financeType: FinanceType) {
        walletFilterState.update {
            it.copy(financeType = financeType)
        }
    }

    private fun updateDateType(dateType: DateType) {
        if (walletFilterState.value.availableYears.isNotEmpty()) {
            val selectedDate = walletFilterState.value.availableYears.last()
            walletFilterState.update {
                it.copy(
                    dateType = dateType,
                    selectedDate = selectedDate,
                )
            }
        }
    }

    private fun incrementSelectedDate() {
        val currentIndex =
            walletFilterState.value.availableYears.indexOf(walletFilterState.value.selectedDate)
        if (currentIndex in 0 until walletFilterState.value.availableYears.size - 1) {
            walletFilterState.update {
                it.copy(selectedDate = walletFilterState.value.availableYears[currentIndex + 1])
            }
        }
    }

    private fun decrementSelectedDate() {
        val currentIndex =
            walletFilterState.value.availableYears.indexOf(walletFilterState.value.selectedDate)
        if (currentIndex in 1 until walletFilterState.value.availableYears.size) {
            walletFilterState.update {
                it.copy(selectedDate = walletFilterState.value.availableYears[currentIndex - 1])
            }
        }
    }

    private fun hideTransaction(id: String) {
        if (lastRemovedTransactionIdState.value != null) {
            clearUndoState()
        }
        shouldDisplayUndoTransactionState.value = true
        lastRemovedTransactionIdState.value = id
    }

    private fun undoTransactionRemoval() {
        lastRemovedTransactionIdState.value = null
        shouldDisplayUndoTransactionState.value = false
    }

    private fun clearUndoState() {
        lastRemovedTransactionIdState.value?.let(::deleteTransaction)
        undoTransactionRemoval()
    }
}

enum class FinanceType {
    NONE,
    EXPENSES,
    INCOME,
}

enum class DateType {
    WEEK,
    MONTH,
    YEAR,
    ALL,
}

data class WalletFilter(
    val selectedCategories: List<Category>,
    val availableCategories: List<Category>,
    val financeType: FinanceType,
    val dateType: DateType,
    val availableYears: List<Int>,
    val selectedDate: Int,
)

sealed interface WalletUiState {

    data object Loading : WalletUiState

    data class Success(
        val currentBalance: BigDecimal,
        val walletFilter: WalletFilter,
        val wallet: Wallet,
        val shouldDisplayUndoTransaction: Boolean,
        val transactionsCategories: List<TransactionWithCategory>,
    ) : WalletUiState
}