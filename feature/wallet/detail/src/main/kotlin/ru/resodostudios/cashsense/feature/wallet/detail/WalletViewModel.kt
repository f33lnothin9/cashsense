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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.domain.GetExtendedUserWalletUseCase
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.model.data.UserWallet
import ru.resodostudios.cashsense.core.ui.util.getCurrentZonedDateTime
import ru.resodostudios.cashsense.core.ui.util.getZonedDateTime
import ru.resodostudios.cashsense.feature.wallet.detail.DateType.ALL
import ru.resodostudios.cashsense.feature.wallet.detail.DateType.MONTH
import ru.resodostudios.cashsense.feature.wallet.detail.DateType.WEEK
import ru.resodostudios.cashsense.feature.wallet.detail.DateType.YEAR
import ru.resodostudios.cashsense.feature.wallet.detail.FinanceType.EXPENSES
import ru.resodostudios.cashsense.feature.wallet.detail.FinanceType.INCOME
import ru.resodostudios.cashsense.feature.wallet.detail.FinanceType.NONE
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.AddToSelectedCategories
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.DecrementSelectedDate
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.IncrementSelectedDate
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.RemoveFromSelectedCategories
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.UpdateDateType
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.UpdateFinanceType
import ru.resodostudios.cashsense.feature.wallet.detail.WalletUiState.Loading
import ru.resodostudios.cashsense.feature.wallet.detail.WalletUiState.Success
import ru.resodostudios.cashsense.feature.wallet.detail.navigation.WalletRoute
import java.math.BigDecimal.ZERO
import java.time.temporal.WeekFields
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val userDataRepository: UserDataRepository,
    savedStateHandle: SavedStateHandle,
    getExtendedUserWallet: GetExtendedUserWalletUseCase,
) : ViewModel() {

    private val walletRoute: WalletRoute = savedStateHandle.toRoute()

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

    private val selectedTransactionIdState = MutableStateFlow<String?>(null)

    val walletUiState: StateFlow<WalletUiState> = combine(
        getExtendedUserWallet.invoke(walletRoute.walletId),
        walletFilterState,
        selectedTransactionIdState,
    ) { extendedUserWallet, walletFilter, selectedTransactionId ->
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
        }

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
                availableCategories = walletFilter.availableCategories,
                selectedCategories = walletFilter.selectedCategories,
                financeType = walletFilter.financeType,
                dateType = walletFilter.dateType,
                availableYears = walletFilter.availableYears,
                availableMonths = walletFilter.availableMonths,
                selectedYear = walletFilter.selectedYear,
                selectedMonth = walletFilter.selectedMonth,
            ),
            userWallet = extendedUserWallet.userWallet,
            selectedTransactionCategory = selectedTransactionId?.let { id ->
                filteredByCategories.find { it.transaction.id == id }
            },
            transactionsCategories = filteredByCategories,
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
            .plus(getCurrentZonedDateTime().year)
            .toSortedSet()
            .toList()
        walletFilterState.update {
            it.copy(availableYears = availableYears)
        }
    }

    private fun calculateAvailableCategories(transactionsCategories: List<TransactionWithCategory>) {
        val availableCategories = transactionsCategories
            .mapNotNull { it.category }
            .toSet()
            .toList()
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
            DecrementSelectedDate -> decrementSelectedDate()
            IncrementSelectedDate -> incrementSelectedDate()
        }
    }

    fun updateTransactionId(id: String) {
        selectedTransactionIdState.value = id
    }

    fun deleteTransaction() {
        viewModelScope.launch {
            selectedTransactionIdState.value?.let { id ->
                val transactionCategory = transactionsRepository.getTransactionWithCategory(id)
                    .first()
                if (transactionCategory.transaction.transferId != null) {
                    transactionsRepository.deleteTransfer(transactionCategory.transaction.transferId!!)
                } else {
                    transactionsRepository.deleteTransaction(id)
                }
            }
        }
    }

    fun updateTransactionIgnoring(ignored: Boolean) {
        viewModelScope.launch {
            selectedTransactionIdState.value?.let { id ->
                val transactionCategory = transactionsRepository.getTransactionWithCategory(id)
                    .first()
                val transaction = transactionCategory.transaction.copy(ignored = ignored)
                transactionsRepository.upsertTransaction(transaction)
            }
        }
    }

    fun setPrimaryWalletId(id: String, isPrimary: Boolean) {
        viewModelScope.launch {
            userDataRepository.setPrimaryWallet(id, isPrimary)
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
        years.find { it == getCurrentZonedDateTime().year } ?: getCurrentZonedDateTime().year

    private fun findCurrentMonth(months: List<Int>) =
        months.find { it == getCurrentZonedDateTime().monthValue }
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
        val selectedTransactionCategory: TransactionWithCategory?,
        val transactionsCategories: List<TransactionWithCategory>,
    ) : WalletUiState
}