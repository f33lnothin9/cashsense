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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.CurrencyConversionRepository
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
import ru.resodostudios.cashsense.core.model.data.FinanceType.NOT_SET
import ru.resodostudios.cashsense.core.model.data.TransactionFilter
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.ui.util.applyTransactionFilter
import ru.resodostudios.cashsense.core.ui.util.getCurrentMonth
import ru.resodostudios.cashsense.core.ui.util.getCurrentYear
import ru.resodostudios.cashsense.core.ui.util.getZonedDateTime
import ru.resodostudios.cashsense.core.ui.util.isInCurrentMonthAndYear
import java.math.BigDecimal
import java.time.YearMonth
import java.util.Currency
import javax.inject.Inject

@HiltViewModel
class TransactionOverviewViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    private val currencyConversionRepository: CurrencyConversionRepository,
    userDataRepository: UserDataRepository,
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

    val financePanelUiState: StateFlow<FinancePanelUiState> = combine(
        getExtendedUserWallets.invoke(),
        userDataRepository.userData,
    ) { wallets, userData ->
        val baseCurrencies = wallets.mapTo(HashSet()) { it.userWallet.currency }
        val userCurrency = Currency.getInstance(userData.currency)
        Triple(baseCurrencies, userCurrency, wallets)
    }
        .flatMapLatest { (baseCurrencies, userCurrency, wallets) ->
            if (baseCurrencies.isEmpty()) {
                flowOf(FinancePanelUiState.NotShown)
            } else {
                combine(
                    currencyConversionRepository.getConvertedCurrencies(
                        baseCurrencies = baseCurrencies,
                        targetCurrency = userCurrency,
                    ),
                    transactionFilterState,
                ) { exchangeRates, transactionFilter ->
                    val exchangeRateMap = exchangeRates
                        .associate { it.baseCurrency to it.exchangeRate }

                    val filterableTransactions = wallets
                        .flatMap { wallet -> wallet.transactionsWithCategories }
                        .applyTransactionFilter(transactionFilter)

                    val filteredTransactions = filterableTransactions
                        .transactionsCategories
                        .filterNot { it.transaction.ignored }
                        .filter {
                            if (transactionFilter.dateType == ALL) {
                                it.transaction.timestamp
                                    .getZonedDateTime()
                                    .isInCurrentMonthAndYear()
                            } else true
                        }

                    val totalBalance = wallets.sumOf {
                        if (userCurrency == it.userWallet.currency) {
                            return@sumOf it.userWallet.currentBalance
                        }
                        val exchangeRate = exchangeRateMap[it.userWallet.currency]
                            ?: return@combine FinancePanelUiState.NotShown

                        it.userWallet.currentBalance * exchangeRate
                    }

                    val (expenses, income) = filteredTransactions
                        .map { it.transaction }
                        .partition { it.amount.signum() < 0 }
                        .let { (expensesList, incomeList) ->
                            val expensesSum = expensesList.sumOf {
                                if (userCurrency == it.currency) {
                                    return@sumOf it.amount
                                }
                                val exchangeRate = exchangeRateMap[it.currency]
                                    ?: return@combine FinancePanelUiState.NotShown

                                it.amount * exchangeRate
                            }.abs()
                            val incomeSum = incomeList.sumOf {
                                if (userCurrency == it.currency) {
                                    return@sumOf it.amount
                                }
                                val exchangeRate = exchangeRateMap[it.currency]
                                    ?: return@combine FinancePanelUiState.NotShown

                                it.amount * exchangeRate
                            }
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

                    FinancePanelUiState.Shown(
                        transactionFilter = transactionFilter,
                        income = income,
                        expenses = expenses,
                        graphData = graphData,
                        userCurrency = userCurrency,
                        availableCategories = filterableTransactions.availableCategories,
                        totalBalance = totalBalance,
                    )
                }
                    .catch { FinancePanelUiState.NotShown }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FinancePanelUiState.Loading,
        )

    val transactionOverviewUiState: StateFlow<TransactionOverviewUiState> = combine(
        getExtendedUserWallets.invoke(),
        transactionFilterState,
        selectedTransactionIdState,
    ) { extendedUserWallets, transactionFilter, selectedTransactionId ->
        val transactions = extendedUserWallets
            .flatMap { it.transactionsWithCategories }
            .sortedByDescending { it.transaction.timestamp }
            .applyTransactionFilter(transactionFilter)
            .transactionsCategories

        TransactionOverviewUiState.Success(
            selectedTransactionCategory = selectedTransactionId?.let { id ->
                transactions.find { it.transaction.id == id }
            },
            transactionsCategories = transactions,
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

sealed interface FinancePanelUiState {

    data object Loading : FinancePanelUiState

    data object NotShown : FinancePanelUiState

    data class Shown(
        val transactionFilter: TransactionFilter,
        val availableCategories: List<Category>,
        val userCurrency: Currency,
        val expenses: BigDecimal,
        val income: BigDecimal,
        val graphData: Map<Int, BigDecimal>,
        val totalBalance: BigDecimal,
    ) : FinancePanelUiState
}

sealed interface TransactionOverviewUiState {

    data object Loading : TransactionOverviewUiState

    data class Success(
        val selectedTransactionCategory: TransactionWithCategory?,
        val transactionsCategories: List<TransactionWithCategory>,
    ) : TransactionOverviewUiState
}