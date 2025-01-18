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

    private val walletFilterState = MutableStateFlow(
        WalletFilter(
            selectedCategories = emptySet(),
            availableCategories = emptyList(),
            financeType = NONE,
            dateType = ALL,
            selectedYearMonth = YearMonth.of(getCurrentYear(), getCurrentMonth()),
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
                .also {
                    walletFilterState.update { it.copy(selectedCategories = emptySet()) }
                }

            EXPENSES -> extendedUserWallet.transactionsWithCategories
                .filter { it.transaction.amount < ZERO }

            INCOME -> extendedUserWallet.transactionsWithCategories
                .filter { it.transaction.amount > ZERO }
        }

        val dateTypeTransactions = when (walletFilter.dateType) {
            ALL -> financeTypeTransactions
            WEEK -> financeTypeTransactions.filter {
                val weekOfTransaction = it.transaction.timestamp
                    .getZonedDateTime()
                    .get(WeekFields.ISO.weekOfWeekBasedYear())
                weekOfTransaction == getCurrentZonedDateTime().get(WeekFields.ISO.weekOfWeekBasedYear())
            }

            MONTH -> financeTypeTransactions.filter {
                it.transaction.timestamp.getZonedDateTime().year == walletFilter.selectedYearMonth.year &&
                        it.transaction.timestamp.getZonedDateTime().monthValue == walletFilter.selectedYearMonth.monthValue
            }

            YEAR -> financeTypeTransactions.filter {
                it.transaction.timestamp.getZonedDateTime().year == walletFilter.selectedYearMonth.year
            }
        }

        val availableCategories = dateTypeTransactions
            .mapNotNull { it.category }
            .distinct()

        val filteredByCategories = if (walletFilter.selectedCategories.isNotEmpty()) {
            dateTypeTransactions
                .filter { walletFilter.selectedCategories.contains(it.category) }
                .also { transactionsCategories ->
                    if (transactionsCategories.isEmpty()) {
                        walletFilterState.update {
                            it.copy(selectedCategories = emptySet())
                        }
                    }
                }
        } else dateTypeTransactions

        Success(
            walletFilter = WalletFilter(
                availableCategories = availableCategories,
                selectedCategories = walletFilter.selectedCategories,
                financeType = walletFilter.financeType,
                dateType = walletFilter.dateType,
                selectedYearMonth = walletFilter.selectedYearMonth,
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
            it.copy(
                selectedCategories = buildSet {
                    addAll(it.selectedCategories)
                    add(category)
                },
            )
        }
    }

    private fun removeFromSelectedCategories(category: Category) {
        walletFilterState.update {
            it.copy(
                selectedCategories = buildSet {
                    addAll(it.selectedCategories)
                    remove(category)
                },
            )
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
                selectedYearMonth = YearMonth.of(getCurrentYear(), getCurrentMonth()),
            )
        }
    }

    private fun incrementSelectedDate() {
        when (walletFilterState.value.dateType) {
            MONTH -> {
                walletFilterState.update {
                    it.copy(
                        selectedYearMonth = it.selectedYearMonth.plusMonths(1),
                    )
                }
            }

            YEAR -> {
                walletFilterState.update {
                    it.copy(
                        selectedYearMonth = it.selectedYearMonth.plusYears(1),
                    )
                }
            }

            ALL, WEEK -> {}
        }
    }

    private fun decrementSelectedDate() {
        when (walletFilterState.value.dateType) {
            MONTH -> {
                walletFilterState.update {
                    it.copy(
                        selectedYearMonth = it.selectedYearMonth.minusMonths(1),
                    )
                }
            }

            YEAR -> {
                walletFilterState.update {
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

data class WalletFilter(
    val selectedCategories: Set<Category>,
    val availableCategories: List<Category>,
    val financeType: FinanceType,
    val dateType: DateType,
    val selectedYearMonth: YearMonth,
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