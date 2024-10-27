package ru.resodostudios.cashsense.feature.wallet

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
import ru.resodostudios.cashsense.core.domain.GetExtendedUserWalletUseCase
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.model.data.UserWallet
import ru.resodostudios.cashsense.core.ui.getCurrentZonedDateTime
import ru.resodostudios.cashsense.core.ui.getZonedDateTime
import ru.resodostudios.cashsense.feature.wallet.DateType.ALL
import ru.resodostudios.cashsense.feature.wallet.DateType.MONTH
import ru.resodostudios.cashsense.feature.wallet.DateType.WEEK
import ru.resodostudios.cashsense.feature.wallet.DateType.YEAR
import ru.resodostudios.cashsense.feature.wallet.FinanceType.EXPENSES
import ru.resodostudios.cashsense.feature.wallet.FinanceType.INCOME
import ru.resodostudios.cashsense.feature.wallet.FinanceType.NONE
import ru.resodostudios.cashsense.feature.wallet.WalletEvent.AddToSelectedCategories
import ru.resodostudios.cashsense.feature.wallet.WalletEvent.ClearUndoState
import ru.resodostudios.cashsense.feature.wallet.WalletEvent.DecrementSelectedDate
import ru.resodostudios.cashsense.feature.wallet.WalletEvent.HideTransaction
import ru.resodostudios.cashsense.feature.wallet.WalletEvent.IncrementSelectedDate
import ru.resodostudios.cashsense.feature.wallet.WalletEvent.RemoveFromSelectedCategories
import ru.resodostudios.cashsense.feature.wallet.WalletEvent.UndoTransactionRemoval
import ru.resodostudios.cashsense.feature.wallet.WalletEvent.UpdateDateType
import ru.resodostudios.cashsense.feature.wallet.WalletEvent.UpdateFinanceType
import ru.resodostudios.cashsense.feature.wallet.WalletUiState.Loading
import ru.resodostudios.cashsense.feature.wallet.WalletUiState.Success
import ru.resodostudios.cashsense.feature.wallet.navigation.WalletRoute
import java.math.BigDecimal.ZERO
import java.time.temporal.WeekFields
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    savedStateHandle: SavedStateHandle,
    getExtendedUserWallet: GetExtendedUserWalletUseCase,
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
            availableMonths = (1..12).toList(),
            selectedYear = 0,
            selectedMonth = 0,
        )
    )

    val walletUiState: StateFlow<WalletUiState> = combine(
        getExtendedUserWallet.invoke(walletRoute.walletId),
        walletFilterState,
        shouldDisplayUndoTransactionState,
        lastRemovedTransactionIdState,
    ) { extendedUserWallet, walletFilter, shouldDisplayUndoTransaction, lastRemovedTransactionId ->
        val financeTypeTransactions = when (walletFilter.financeType) {
            NONE -> extendedUserWallet.transactionsWithCategories
            EXPENSES -> extendedUserWallet.transactionsWithCategories.filter { it.transaction.amount < ZERO }
            INCOME -> extendedUserWallet.transactionsWithCategories.filter { it.transaction.amount > ZERO }
        }
        calculateAvailableYears(financeTypeTransactions)
        val dateTypeTransactions = when (walletFilter.dateType) {
            ALL -> financeTypeTransactions
            WEEK -> financeTypeTransactions.filter {
                val weekOfTransaction = it.transaction.timestamp
                    .getZonedDateTime()
                    .get(WeekFields.ISO.weekOfWeekBasedYear())
                weekOfTransaction == getCurrentZonedDateTime().get(WeekFields.ISO.weekOfWeekBasedYear())
            }

            MONTH -> financeTypeTransactions
                .filter { it.transaction.timestamp.getZonedDateTime().year == walletFilter.selectedYear }
                .filter { it.transaction.timestamp.getZonedDateTime().monthValue == walletFilter.selectedMonth }

            YEAR -> financeTypeTransactions
                .filter { it.transaction.timestamp.getZonedDateTime().year == walletFilter.selectedYear }
        }.filterNot { it.transaction.id == lastRemovedTransactionId }

        calculateAvailableCategories(dateTypeTransactions)
        val filteredByCategories = if (walletFilter.selectedCategories.isNotEmpty()) {
            dateTypeTransactions
                .filter { walletFilter.selectedCategories.contains(it.category) }
                .apply {
                    if (this.isEmpty()) {
                        walletFilterState.update {
                            it.copy(selectedCategories = emptyList())
                        }
                    }
                }
        } else dateTypeTransactions

        Success(
            walletFilter = WalletFilter(
                availableCategories = walletFilter.availableCategories.minus(Category()),
                selectedCategories = walletFilter.selectedCategories,
                financeType = walletFilter.financeType,
                dateType = walletFilter.dateType,
                availableYears = walletFilter.availableYears,
                availableMonths = walletFilter.availableMonths,
                selectedYear = walletFilter.selectedYear,
                selectedMonth = walletFilter.selectedMonth,
            ),
            userWallet = extendedUserWallet.userWallet,
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

    private fun calculateAvailableYears(transactions: List<TransactionWithCategory>) {
        val availableYears = transactions
            .map { it.transaction.timestamp.getZonedDateTime().year }
            .toSortedSet()
            .toList()
        walletFilterState.update {
            it.copy(availableYears = availableYears)
        }
    }

    private fun calculateAvailableCategories(transactionsCategories: List<TransactionWithCategory>) {
        val availableCategories = transactionsCategories
            .map { it.category }
            .toSet()
            .filterNotNull()
        walletFilterState.update {
            it.copy(availableCategories = availableCategories)
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
        walletFilterState.update {
            it.copy(
                dateType = dateType,
                selectedYear = findCurrentYear(walletFilterState.value.availableYears),
                selectedMonth = findCurrentMonth(walletFilterState.value.availableMonths),
            )
        }
    }

    private fun findCurrentYear(years: List<Int>) =
        years.firstOrNull { it == getCurrentZonedDateTime().year } ?: getCurrentZonedDateTime().year

    private fun findCurrentMonth(months: List<Int>) =
        months.firstOrNull { it == getCurrentZonedDateTime().monthValue }
            ?: getCurrentZonedDateTime().monthValue

    private fun incrementSelectedDate() {
        val walletFilter = walletFilterState.value
        val yearIndex = walletFilter.availableYears.indexOf(walletFilter.selectedYear)
        val monthIndex = walletFilter.availableMonths.indexOf(walletFilter.selectedMonth)
        when (walletFilter.dateType) {
            MONTH -> {
                if (monthIndex in 0 until walletFilter.availableMonths.size - 1) {
                    walletFilterState.update {
                        it.copy(selectedMonth = walletFilter.availableMonths[monthIndex + 1])
                    }
                }
            }

            YEAR -> {
                if (yearIndex in 0 until walletFilter.availableYears.size - 1) {
                    walletFilterState.update {
                        it.copy(selectedYear = walletFilter.availableYears[yearIndex + 1])
                    }
                }
            }

            else -> {}
        }
    }

    private fun decrementSelectedDate() {
        val walletFilter = walletFilterState.value
        val yearIndex = walletFilter.availableYears.indexOf(walletFilter.selectedYear)
        val monthIndex = walletFilter.availableMonths.indexOf(walletFilter.selectedMonth)
        when (walletFilter.dateType) {
            MONTH -> {
                if (monthIndex in 1 until walletFilter.availableMonths.size) {
                    walletFilterState.update {
                        it.copy(selectedMonth = walletFilter.availableMonths[monthIndex - 1])
                    }
                }
            }

            YEAR -> {
                if (yearIndex in 1 until walletFilter.availableYears.size) {
                    walletFilterState.update {
                        it.copy(selectedYear = walletFilter.availableYears[yearIndex - 1])
                    }
                }
            }

            else -> {}
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
    val availableMonths: List<Int>,
    val selectedYear: Int,
    val selectedMonth: Int,
)

sealed interface WalletUiState {

    data object Loading : WalletUiState

    data class Success(
        val walletFilter: WalletFilter,
        val userWallet: UserWallet,
        val shouldDisplayUndoTransaction: Boolean,
        val transactionsCategories: List<TransactionWithCategory>,
    ) : WalletUiState
}