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
import ru.resodostudios.cashsense.core.model.data.DateType
import ru.resodostudios.cashsense.core.model.data.DateType.ALL
import ru.resodostudios.cashsense.core.model.data.DateType.MONTH
import ru.resodostudios.cashsense.core.model.data.DateType.WEEK
import ru.resodostudios.cashsense.core.model.data.DateType.YEAR
import ru.resodostudios.cashsense.core.model.data.FinanceType
import ru.resodostudios.cashsense.core.model.data.FinanceType.EXPENSES
import ru.resodostudios.cashsense.core.model.data.FinanceType.NOT_SET
import ru.resodostudios.cashsense.core.model.data.TransactionFilter
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.model.data.UserWallet
import ru.resodostudios.cashsense.core.ui.component.getFinanceProgress
import ru.resodostudios.cashsense.core.ui.util.applyTransactionFilter
import ru.resodostudios.cashsense.core.ui.util.getCurrentMonth
import ru.resodostudios.cashsense.core.ui.util.getCurrentYear
import ru.resodostudios.cashsense.core.ui.util.getZonedDateTime
import ru.resodostudios.cashsense.core.ui.util.isInCurrentMonthAndYear
import ru.resodostudios.cashsense.feature.wallet.detail.navigation.WalletRoute
import java.math.BigDecimal
import java.time.YearMonth
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
            financeType = NOT_SET,
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
        val filterableTransactions = extendedUserWallet.transactionsWithCategories
            .applyTransactionFilter(transactionFilter)

        val filteredTransactions = filterableTransactions.transactionsCategories
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
                expensesSum to incomeSum
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
            incomeProgress = getFinanceProgress(income, extendedUserWallet.userWallet.currentBalance),
            expenses = expenses,
            expensesProgress = getFinanceProgress(expenses, extendedUserWallet.userWallet.currentBalance),
            graphData = graphData,
            userWallet = extendedUserWallet.userWallet,
            selectedTransactionCategory = selectedTransactionId?.let { id ->
                filterableTransactions.transactionsCategories.find { it.transaction.id == id }
            },
            transactionsCategories = filterableTransactions.transactionsCategories,
            availableCategories = filterableTransactions.availableCategories,
        )
    }
        .catch { WalletUiState.Loading }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WalletUiState.Loading,
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

    fun setPrimaryWalletId(id: String, isPrimary: Boolean) {
        viewModelScope.launch {
            userDataRepository.setPrimaryWallet(id, isPrimary)
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
        if (financeType == NOT_SET) {
            transactionFilterState.update { it.copy(selectedCategories = emptySet()) }
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

    fun updateSelectedDate(increment: Short) {
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
        val incomeProgress: Float,
        val expensesProgress: Float,
        val graphData: Map<Int, BigDecimal>,
    ) : WalletUiState
}