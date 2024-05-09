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
import ru.resodostudios.cashsense.feature.wallet.detail.DateType.ALL
import ru.resodostudios.cashsense.feature.wallet.detail.DateType.MONTH
import ru.resodostudios.cashsense.feature.wallet.detail.DateType.WEEK
import ru.resodostudios.cashsense.feature.wallet.detail.DateType.YEAR
import ru.resodostudios.cashsense.feature.wallet.detail.FinanceSectionType.EXPENSES
import ru.resodostudios.cashsense.feature.wallet.detail.FinanceSectionType.INCOME
import ru.resodostudios.cashsense.feature.wallet.detail.FinanceSectionType.NONE
import ru.resodostudios.cashsense.feature.wallet.detail.navigation.WalletDestination
import java.math.BigDecimal
import java.time.temporal.WeekFields
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository,
    walletsRepository: WalletsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val walletDestination: WalletDestination = savedStateHandle.toRoute()

    private val shouldDisplayUndoTransactionState = MutableStateFlow(false)
    private val lastRemovedTransactionIdState = MutableStateFlow<String?>(null)

    private val walletFilterState = MutableStateFlow(WalletFilterState())

    val walletUiState: StateFlow<WalletUiState> = combine(
        walletsRepository.getWalletWithTransactions(walletDestination.id),
        walletFilterState,
        shouldDisplayUndoTransactionState,
        lastRemovedTransactionIdState,
    ) { walletTransactionsCategories, walletFilter, shouldDisplayUndoTransaction, lastRemovedTransactionId ->
        val wallet = walletTransactionsCategories.wallet
        val transactionsCategories = walletTransactionsCategories.transactionsWithCategories

        val currentBalance = wallet.initialBalance.plus(
            transactionsCategories.sumOf { it.transaction.amount }
        )
        val sortedTransactions =
            transactionsCategories.sortedByDescending { it.transaction.timestamp }
        val filteredTransactionsCategories = when (walletFilter.financeType) {
            NONE -> sortedTransactions
            EXPENSES -> calculateTransactionsCategories(
                sortedTransactions.filter { it.transaction.amount < BigDecimal.ZERO }
            )

            INCOME -> calculateTransactionsCategories(
                sortedTransactions.filter { it.transaction.amount > BigDecimal.ZERO }
            )
        }
            .run {
                when (walletFilter.dateType) {
                    ALL -> this
                    WEEK -> filter {
                        it.transaction.timestamp
                            .getZonedDateTime()
                            .get(WeekFields.ISO.weekOfWeekBasedYear()) ==
                                getCurrentZonedDateTime().get(WeekFields.ISO.weekOfWeekBasedYear())
                    }

                    MONTH -> filter { it.transaction.timestamp.getZonedDateTime().month == getCurrentZonedDateTime().month }
                    YEAR -> filter { it.transaction.timestamp.getZonedDateTime().year == getCurrentZonedDateTime().year }
                }

            }
            .filterNot { it.transaction.id == lastRemovedTransactionId }

        WalletUiState.Success(
            currentBalance = currentBalance,
            availableCategories = walletFilter.availableCategories.minus(Category()),
            selectedCategories = walletFilter.selectedCategories,
            financeSectionType = walletFilter.financeType,
            dateType = walletFilter.dateType,
            wallet = wallet,
            transactionsCategories = filteredTransactionsCategories,
            shouldDisplayUndoTransaction = shouldDisplayUndoTransaction,
        )
    }
        .catch { WalletUiState.Loading }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WalletUiState.Loading,
        )

    private fun calculateTransactionsCategories(transactionsCategories: List<TransactionWithCategory>): List<TransactionWithCategory> {
        transactionsCategories
            .map { it.category }
            .toSet()
            .also { categories ->
                walletFilterState.update {
                    it.copy(availableCategories = categories.filterNotNull())
                }
            }
        return if (walletFilterState.value.selectedCategories.isNotEmpty()) {
            transactionsCategories
                .filter {
                    walletFilterState.value.selectedCategories.contains(it.category)
                }
                .apply {
                    if (this.isEmpty()) {
                        walletFilterState.update {
                            it.copy(selectedCategories = emptyList())
                        }
                    }
                }
        } else transactionsCategories
    }

    private fun deleteTransaction(id: String) {
        viewModelScope.launch {
            transactionsRepository.deleteTransaction(id)
        }
    }

    fun addToSelectedCategories(category: Category) {
        walletFilterState.update {
            it.copy(selectedCategories = it.selectedCategories.plus(category))
        }
    }

    fun removeFromSelectedCategories(category: Category) {
        walletFilterState.update {
            it.copy(selectedCategories = it.selectedCategories.minus(category))
        }
    }

    fun updateFinanceType(financeType: FinanceSectionType) {
        walletFilterState.update {
            it.copy(financeType = financeType)
        }
    }

    fun updateDateType(dateType: DateType) {
        walletFilterState.update {
            it.copy(dateType = dateType)
        }
    }

    fun hideTransaction(id: String) {
        if (lastRemovedTransactionIdState.value != null) {
            clearUndoState()
        }
        shouldDisplayUndoTransactionState.value = true
        lastRemovedTransactionIdState.value = id
    }

    fun undoTransactionRemoval() {
        lastRemovedTransactionIdState.value = null
        shouldDisplayUndoTransactionState.value = false
    }

    fun clearUndoState() {
        lastRemovedTransactionIdState.value?.let(::deleteTransaction)
        undoTransactionRemoval()
    }
}

enum class FinanceSectionType {
    NONE,
    EXPENSES,
    INCOME,
}

enum class DateType {
    ALL,
    WEEK,
    MONTH,
    YEAR,
}

data class WalletFilterState(
    val selectedCategories: List<Category> = emptyList(),
    val availableCategories: List<Category> = emptyList(),
    val financeType: FinanceSectionType = NONE,
    val dateType: DateType = ALL,
)

sealed interface WalletUiState {

    data object Loading : WalletUiState

    data class Success(
        val currentBalance: BigDecimal,
        val availableCategories: List<Category>,
        val selectedCategories: List<Category>,
        val financeSectionType: FinanceSectionType,
        val dateType: DateType,
        val wallet: Wallet,
        val shouldDisplayUndoTransaction: Boolean,
        val transactionsCategories: List<TransactionWithCategory>,
    ) : WalletUiState
}