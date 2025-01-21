package ru.resodostudios.cashsense.feature.transaction.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import ru.resodostudios.cashsense.core.domain.GetExtendedUserWalletsUseCase
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.DateType
import ru.resodostudios.cashsense.core.model.data.DateType.ALL
import ru.resodostudios.cashsense.core.model.data.DateType.MONTH
import ru.resodostudios.cashsense.core.model.data.DateType.WEEK
import ru.resodostudios.cashsense.core.model.data.DateType.YEAR
import ru.resodostudios.cashsense.core.model.data.FinanceType
import ru.resodostudios.cashsense.core.model.data.FinanceType.EXPENSES
import ru.resodostudios.cashsense.core.model.data.FinanceType.INCOME
import ru.resodostudios.cashsense.core.model.data.FinanceType.NOT_SET
import ru.resodostudios.cashsense.core.model.data.TransactionFilter
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.ui.util.getCurrentMonth
import ru.resodostudios.cashsense.core.ui.util.getCurrentYear
import ru.resodostudios.cashsense.core.ui.util.getCurrentZonedDateTime
import ru.resodostudios.cashsense.core.ui.util.getZonedDateTime
import ru.resodostudios.cashsense.core.ui.util.isInCurrentMonthAndYear
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.time.YearMonth
import java.time.temporal.WeekFields
import javax.inject.Inject

@HiltViewModel
class TransactionOverviewViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val userDataRepository: UserDataRepository,
    getExtendedUserWallets: GetExtendedUserWalletsUseCase,
) : ViewModel() {

    private val transactionFilterState = MutableStateFlow(
        TransactionFilter(
            selectedCategories = emptySet(),
            financeType = NOT_SET,
            dateType = ALL,
            selectedYearMonth = YearMonth.of(getCurrentYear(), getCurrentMonth()),
        )
    )

    private val selectedTransactionIdState = MutableStateFlow<String?>(null)

    val transactionOverviewUiState: StateFlow<TransactionOverviewUiState> = combine(
        getExtendedUserWallets.invoke(),
        transactionFilterState,
        selectedTransactionIdState,
    ) { extendedUserWallets, transactionFilter, selectedTransactionId ->
        val allTransactions = extendedUserWallets
            .flatMap { it.transactionsWithCategories }
            .sortedByDescending { it.transaction.timestamp }
        val financeTypeTransactions = when (transactionFilter.financeType) {
            NOT_SET -> allTransactions
                .also { transactionFilterState.update { it.copy(selectedCategories = emptySet()) } }

            EXPENSES -> allTransactions
                .filter { it.transaction.amount < ZERO }

            INCOME -> allTransactions
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

        TransactionOverviewUiState.Success(
            transactionFilter = TransactionFilter(
                selectedCategories = transactionFilter.selectedCategories,
                financeType = transactionFilter.financeType,
                dateType = transactionFilter.dateType,
                selectedYearMonth = transactionFilter.selectedYearMonth,
            ),
            income = income,
            expenses = expenses,
            graphData = graphData,
            selectedTransactionCategory = selectedTransactionId?.let { id ->
                filteredByCategories.find { it.transaction.id == id }
            },
            transactionsCategories = filteredByCategories,
            availableCategories = availableCategories,
        )
    }
        .catch { TransactionOverviewUiState.Loading }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TransactionOverviewUiState.Loading,
        )

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

    fun addToSelectedCategories(category: Category) {
        transactionFilterState.update {
            it.copy(
                selectedCategories = it.selectedCategories.plus(category),
            )
        }
    }

    fun removeFromSelectedCategories(category: Category) {
        transactionFilterState.update {
            it.copy(
                selectedCategories = it.selectedCategories.minus(category),
            )
        }
    }

    fun updateFinanceType(financeType: FinanceType) {
        transactionFilterState.update {
            it.copy(financeType = financeType)
        }
    }

    fun updateDateType(dateType: DateType) {
        transactionFilterState.update {
            it.copy(
                dateType = dateType,
                selectedYearMonth = YearMonth.of(getCurrentYear(), getCurrentMonth()),
            )
        }
    }

    fun updateSelectedDate(increment: Int) {
        when (transactionFilterState.value.dateType) {
            MONTH -> {
                transactionFilterState.update {
                    it.copy(
                        selectedYearMonth = it.selectedYearMonth.plusMonths(increment.toLong()),
                    )
                }
            }

            YEAR -> {
                transactionFilterState.update {
                    it.copy(
                        selectedYearMonth = it.selectedYearMonth.plusYears(increment.toLong()),
                    )
                }
            }

            ALL, WEEK -> {}
        }
    }
}

sealed interface TransactionOverviewUiState {

    data object Loading : TransactionOverviewUiState

    data class Success(
        val transactionFilter: TransactionFilter,
        val selectedTransactionCategory: TransactionWithCategory?,
        val transactionsCategories: List<TransactionWithCategory>,
        val availableCategories: List<Category>,
        val expenses: BigDecimal,
        val income: BigDecimal,
        val graphData: Map<Int, BigDecimal>,
    ) : TransactionOverviewUiState
}