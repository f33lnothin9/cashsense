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
import ru.resodostudios.cashsense.core.ui.util.getCurrentMonth
import ru.resodostudios.cashsense.core.ui.util.getCurrentYear
import ru.resodostudios.cashsense.core.ui.util.getCurrentZonedDateTime
import ru.resodostudios.cashsense.core.ui.util.getZonedDateTime
import ru.resodostudios.cashsense.core.ui.util.isInCurrentMonthAndYear
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
import ru.resodostudios.cashsense.feature.wallet.detail.navigation.WalletRoute
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.time.YearMonth
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

    private val transactionFilterState = MutableStateFlow(
        TransactionFilter(
            selectedCategories = emptySet(),
            financeType = NONE,
            dateType = ALL,
            selectedYearMonth = YearMonth.of(getCurrentYear(), getCurrentMonth()),
        )
    )

    private val selectedTransactionIdState = MutableStateFlow<String?>(null)

    val walletUiState: StateFlow<WalletUiState> = combine(
        getExtendedUserWallet.invoke(walletRoute.walletId),
        transactionFilterState,
        selectedTransactionIdState,
    ) { extendedUserWallet, transactionFilter, selectedTransactionId ->
        val financeTypeTransactions = when (transactionFilter.financeType) {
            NONE -> extendedUserWallet.transactionsWithCategories
                .also { transactionFilterState.update { it.copy(selectedCategories = emptySet()) } }

            EXPENSES -> extendedUserWallet.transactionsWithCategories
                .filter { it.transaction.amount < ZERO }

            INCOME -> extendedUserWallet.transactionsWithCategories
                .filter { it.transaction.amount > ZERO }
        }

        val dateTypeTransactions = when (transactionFilter.dateType) {
            ALL -> financeTypeTransactions
            WEEK -> financeTypeTransactions.filter {
                val weekOfTransaction = it.transaction.timestamp
                    .getZonedDateTime()
                    .get(WeekFields.ISO.weekOfWeekBasedYear())
                weekOfTransaction == getCurrentZonedDateTime().get(WeekFields.ISO.weekOfWeekBasedYear())
            }

            MONTH -> financeTypeTransactions.filter {
                it.transaction.timestamp.getZonedDateTime().year == transactionFilter.selectedYearMonth.year &&
                        it.transaction.timestamp.getZonedDateTime().monthValue == transactionFilter.selectedYearMonth.monthValue
            }

            YEAR -> financeTypeTransactions.filter {
                it.transaction.timestamp.getZonedDateTime().year == transactionFilter.selectedYearMonth.year
            }
        }

        val availableCategories = dateTypeTransactions
            .mapNotNull { it.category }
            .distinct()

        val filteredByCategories = if (transactionFilter.selectedCategories.isNotEmpty()) {
            dateTypeTransactions
                .filter { transactionFilter.selectedCategories.contains(it.category) }
                .also { transactionsCategories ->
                    if (transactionsCategories.isEmpty()) {
                        transactionFilterState.update {
                            it.copy(selectedCategories = emptySet())
                        }
                    }
                }
        } else dateTypeTransactions

        val filteredTransactions = filteredByCategories
            .filterNot { it.transaction.ignored }
            .filter {
                if (transactionFilter.dateType == ALL) {
                    it.transaction.timestamp
                        .getZonedDateTime()
                        .isInCurrentMonthAndYear()
                } else true
            }
        val (expenses, income) = filteredTransactions.partition { it.transaction.amount.signum() < 0 }
            .let { (expensesList, incomeList) ->
                val expensesSum = expensesList.sumOf { it.transaction.amount.abs() }
                val incomeSum = incomeList.sumOf { it.transaction.amount }
                Pair(expensesSum, incomeSum)
            }
        val groupedTransactions = filteredTransactions
            .groupBy {
                val zonedDateTime = it.transaction.timestamp.getZonedDateTime()
                when (transactionFilter.dateType) {
                    YEAR -> zonedDateTime.monthValue
                    MONTH -> zonedDateTime.dayOfMonth
                    ALL, WEEK -> zonedDateTime.dayOfWeek.value
                }
            }
        val graphData = groupedTransactions
            .map { transactionsCategories ->
                transactionsCategories.key to transactionsCategories.value
                    .map { transactionCategory -> transactionCategory.transaction.amount }
                    .run {
                        sumOf {
                            when (transactionFilter.financeType) {
                                EXPENSES -> it.abs()
                                else -> it
                            }
                        }
                    }
            }
            .associate { it.first to it.second }

        WalletUiState.Success(
            transactionFilter = TransactionFilter(
                selectedCategories = transactionFilter.selectedCategories,
                financeType = transactionFilter.financeType,
                dateType = transactionFilter.dateType,
                selectedYearMonth = transactionFilter.selectedYearMonth,
            ),
            income = income,
            expenses = expenses,
            graphData = graphData,
            userWallet = extendedUserWallet.userWallet,
            selectedTransactionCategory = selectedTransactionId?.let { id ->
                filteredByCategories.find { it.transaction.id == id }
            },
            transactionsCategories = filteredByCategories,
            availableCategories = availableCategories,
        )
    }
        .catch { WalletUiState.Loading }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WalletUiState.Loading,
        )

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
        transactionFilterState.update {
            it.copy(
                selectedCategories = buildSet {
                    addAll(it.selectedCategories)
                    add(category)
                },
            )
        }
    }

    private fun removeFromSelectedCategories(category: Category) {
        transactionFilterState.update {
            it.copy(
                selectedCategories = buildSet {
                    addAll(it.selectedCategories)
                    remove(category)
                },
            )
        }
    }

    private fun updateFinanceType(financeType: FinanceType) {
        transactionFilterState.update {
            it.copy(financeType = financeType)
        }
    }

    private fun updateDateType(dateType: DateType) {
        transactionFilterState.update {
            it.copy(
                dateType = dateType,
                selectedYearMonth = YearMonth.of(getCurrentYear(), getCurrentMonth()),
            )
        }
    }

    private fun incrementSelectedDate() {
        when (transactionFilterState.value.dateType) {
            MONTH -> {
                transactionFilterState.update {
                    it.copy(
                        selectedYearMonth = it.selectedYearMonth.plusMonths(1),
                    )
                }
            }

            YEAR -> {
                transactionFilterState.update {
                    it.copy(
                        selectedYearMonth = it.selectedYearMonth.plusYears(1),
                    )
                }
            }

            ALL, WEEK -> {}
        }
    }

    private fun decrementSelectedDate() {
        when (transactionFilterState.value.dateType) {
            MONTH -> {
                transactionFilterState.update {
                    it.copy(
                        selectedYearMonth = it.selectedYearMonth.minusMonths(1),
                    )
                }
            }

            YEAR -> {
                transactionFilterState.update {
                    it.copy(
                        selectedYearMonth = it.selectedYearMonth.minusYears(1),
                    )
                }
            }

            ALL, WEEK -> {}
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

data class TransactionFilter(
    val selectedCategories: Set<Category>,
    val financeType: FinanceType,
    val dateType: DateType,
    val selectedYearMonth: YearMonth,
)

sealed interface WalletUiState {

    data object Loading : WalletUiState

    data class Success(
        val transactionFilter: TransactionFilter,
        val userWallet: UserWallet,
        val selectedTransactionCategory: TransactionWithCategory?,
        val transactionsCategories: List<TransactionWithCategory>,
        val availableCategories: List<Category>,
        val expenses: BigDecimal,
        val income: BigDecimal,
        val graphData: Map<Int, BigDecimal>,
    ) : WalletUiState
}