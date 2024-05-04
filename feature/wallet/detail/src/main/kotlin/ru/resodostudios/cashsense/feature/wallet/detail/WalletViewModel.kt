package ru.resodostudios.cashsense.feature.wallet.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
    savedStateHandle: SavedStateHandle,
    walletsRepository: WalletsRepository,
) : ViewModel() {

    private val walletDestination: WalletDestination = savedStateHandle.toRoute()

    private val selectedCategoriesState = MutableStateFlow<List<Category>>(emptyList())
    private val availableCategoriesState = MutableStateFlow<List<Category>>(emptyList())
    private val financeTypeState = MutableStateFlow(NONE)
    private val dateTypeState = MutableStateFlow(ALL)

    val walletUiState: StateFlow<WalletUiState> = combine(
        selectedCategoriesState.asStateFlow(),
        availableCategoriesState.asStateFlow(),
        financeTypeState.asStateFlow(),
        dateTypeState.asStateFlow(),
        walletsRepository.getWalletWithTransactions(walletDestination.id),
    ) { selectedCategories, availableCategories, financeType, dateType, walletTransactionsCategories ->
        val wallet = walletTransactionsCategories.wallet
        val transactionsCategories = walletTransactionsCategories.transactionsWithCategories

        val currentBalance = wallet.initialBalance.plus(
            transactionsCategories.sumOf { it.transaction.amount }
        )
        val sortedTransactions = transactionsCategories.sortedByDescending { it.transaction.timestamp }
        val filteredTransactionsCategories = when (financeType) {
            NONE -> sortedTransactions
            EXPENSES -> calculateTransactionsCategories(
                sortedTransactions.filter { it.transaction.amount < BigDecimal.ZERO }
            )

            INCOME -> calculateTransactionsCategories(
                sortedTransactions.filter { it.transaction.amount > BigDecimal.ZERO }
            )
        }.run {
            when (dateType) {
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

        WalletUiState.Success(
            currentBalance = currentBalance,
            availableCategories = availableCategories.minus(Category()),
            selectedCategories = selectedCategories,
            financeSectionType = financeType,
            dateType = dateType,
            wallet = wallet,
            transactionsCategories = filteredTransactionsCategories,
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
            is WalletEvent.AddToSelectedCategories -> {
                selectedCategoriesState.update { it.plus(event.category) }
            }

            is WalletEvent.RemoveFromSelectedCategories -> {
                selectedCategoriesState.update { it.minus(event.category) }
            }

            is WalletEvent.UpdateFinanceType -> {
                financeTypeState.update { event.financeSectionType }
            }

            is WalletEvent.UpdateDateType -> {
                dateTypeState.update { event.dateType }
            }
        }
    }

    private fun calculateTransactionsCategories(transactionsCategories: List<TransactionWithCategory>): List<TransactionWithCategory> {
        transactionsCategories
            .map { it.category }
            .toSet()
            .toList()
            .also { availableCategoriesState.value = it.filterNotNull() }
        return if (selectedCategoriesState.value.isNotEmpty()) {
            transactionsCategories
                .filter { selectedCategoriesState.value.contains(it.category) }
                .apply { if (this.isEmpty()) selectedCategoriesState.value = emptyList() }
        } else {
            transactionsCategories
        }
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

sealed interface WalletUiState {

    data object Loading : WalletUiState

    data class Success(
        val currentBalance: BigDecimal,
        val availableCategories: List<Category>,
        val selectedCategories: List<Category>,
        val financeSectionType: FinanceSectionType,
        val dateType: DateType,
        val wallet: Wallet,
        val transactionsCategories: List<TransactionWithCategory>,
    ) : WalletUiState
}