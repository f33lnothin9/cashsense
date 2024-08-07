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
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.AddToSelectedCategories
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.ClearUndoState
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.HideTransaction
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.RemoveFromSelectedCategories
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.UndoTransactionRemoval
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.UpdateDateType
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.UpdateFinanceType
import ru.resodostudios.cashsense.feature.wallet.detail.WalletUiState.Loading
import ru.resodostudios.cashsense.feature.wallet.detail.WalletUiState.Success
import ru.resodostudios.cashsense.feature.wallet.detail.navigation.WalletRoute
import java.math.BigDecimal
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

    private val walletFilterState = MutableStateFlow(WalletFilterState())

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
            EXPENSES -> calculateTransactionsCategories(
                sortedTransactions.filter { it.transaction.amount < BigDecimal.ZERO }
            )

            INCOME -> calculateTransactionsCategories(
                sortedTransactions.filter { it.transaction.amount > BigDecimal.ZERO }
            )
        }
        val dateTypeTransactions = when (walletFilter.dateType) {
            ALL -> financeTypeTransactions
            WEEK -> financeTypeTransactions.filter {
                it.transaction.timestamp
                    .getZonedDateTime()
                    .get(WeekFields.ISO.weekOfWeekBasedYear()) ==
                        getCurrentZonedDateTime().get(WeekFields.ISO.weekOfWeekBasedYear())
            }

            MONTH -> financeTypeTransactions
                .filter { it.transaction.timestamp.getZonedDateTime().month == getCurrentZonedDateTime().month }

            YEAR -> financeTypeTransactions
                .filter { it.transaction.timestamp.getZonedDateTime().year == getCurrentZonedDateTime().year }
        }
        val filteredTransactionsCategories = dateTypeTransactions
            .filterNot { it.transaction.id == lastRemovedTransactionId }

        Success(
            currentBalance = currentBalance,
            availableCategories = walletFilter.availableCategories.minus(Category()),
            selectedCategories = walletFilter.selectedCategories,
            financeSectionType = walletFilter.financeType,
            dateType = walletFilter.dateType,
            wallet = walletTransactionsCategories.wallet,
            transactionsCategories = filteredTransactionsCategories,
            shouldDisplayUndoTransaction = shouldDisplayUndoTransaction,
        )
    }
        .catch { Loading }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Loading,
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
                .filter { walletFilterState.value.selectedCategories.contains(it.category) }
                .apply {
                    if (this.isEmpty()) {
                        walletFilterState.update {
                            it.copy(selectedCategories = emptyList())
                        }
                    }
                }
        } else transactionsCategories
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

    private fun updateFinanceType(financeType: FinanceSectionType) {
        walletFilterState.update {
            it.copy(financeType = financeType)
        }
    }

    private fun updateDateType(dateType: DateType) {
        walletFilterState.update {
            it.copy(dateType = dateType)
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