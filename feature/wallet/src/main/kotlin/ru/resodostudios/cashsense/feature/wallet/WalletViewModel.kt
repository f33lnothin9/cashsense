package ru.resodostudios.cashsense.feature.wallet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import ru.resodostudios.cashsense.core.model.data.WalletWithTransactionsAndCategories
import ru.resodostudios.cashsense.feature.wallet.navigation.WalletArgs
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    walletsRepository: WalletsRepository,
) : ViewModel() {

    private val walletArgs: WalletArgs = WalletArgs(savedStateHandle)

    private val selectedCategoriesState = MutableStateFlow<List<Category>>(emptyList())
    private val currentFinanceTypeState = MutableStateFlow(FinanceType.DEFAULT)

    val walletUiState: StateFlow<WalletUiState> = combine(
        selectedCategoriesState.asStateFlow(),
        currentFinanceTypeState.asStateFlow(),
        walletsRepository.getWalletWithTransactions(walletArgs.walletId),
    ) { selectedCategories, currentFinanceType, walletTransactionsCategories ->
        val currentBalance = walletTransactionsCategories.wallet.initialBalance.plus(
            walletTransactionsCategories.transactionsWithCategories.sumOf { it.transaction.amount }
        )
        val sortedTransactions =
            walletTransactionsCategories.transactionsWithCategories.sortedByDescending {
                it.transaction.date
            }
        var availableCategories = sortedTransactions
            .map { it.category }
            .toSet()
            .toList()

        val expenses = sortedTransactions.filter { it.transaction.amount < BigDecimal.ZERO }
        val income = sortedTransactions.filter { it.transaction.amount > BigDecimal.ZERO }

        val transactionsCategories = when (currentFinanceType) {
            FinanceType.DEFAULT -> sortedTransactions
            FinanceType.EXPENSES -> {
                availableCategories = expenses
                    .map { it.category }
                    .toSet()
                    .toList()
                if (selectedCategories.isNotEmpty()) {
                    expenses
                        .filter { selectedCategories.contains(it.category) }
                        .apply { if (this.isEmpty()) selectedCategoriesState.value = emptyList() }
                } else {
                    expenses
                }
            }

            FinanceType.INCOME -> {
                availableCategories = income
                    .map { it.category }
                    .toSet()
                    .toList()
                if (selectedCategories.isNotEmpty()) {
                    income
                        .filter { selectedCategories.contains(it.category) }
                        .apply { if (this.isEmpty()) selectedCategoriesState.value = emptyList() }
                } else {
                    income
                }
            }
        }
        val walletData = WalletWithTransactionsAndCategories(
            wallet = walletTransactionsCategories.wallet,
            transactionsWithCategories = transactionsCategories
        )

        WalletUiState.Success(
            currentBalance = currentBalance,
            availableCategories = availableCategories,
            selectedCategories = selectedCategories,
            currentFinanceType = currentFinanceType,
            walletTransactionsCategories = walletData,
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
                currentFinanceTypeState.update { event.financeType }
            }
        }
    }
}

enum class FinanceType {
    DEFAULT,
    EXPENSES,
    INCOME,
}

sealed interface WalletUiState {

    data object Loading : WalletUiState

    data class Success(
        val currentBalance: BigDecimal,
        val availableCategories: List<Category?>,
        val selectedCategories: List<Category>,
        val currentFinanceType: FinanceType,
        val walletTransactionsCategories: WalletWithTransactionsAndCategories,
    ) : WalletUiState
}