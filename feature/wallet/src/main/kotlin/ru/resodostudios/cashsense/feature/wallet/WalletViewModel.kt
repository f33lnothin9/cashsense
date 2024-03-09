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

    val walletUiState: StateFlow<WalletUiState> = combine(
        selectedCategoriesState.asStateFlow(),
        walletsRepository.getWalletWithTransactions(walletArgs.walletId),
    ) { selectedCategories, walletTransactionsCategories ->
        val currentBalance = walletTransactionsCategories.wallet.initialBalance
            .plus(walletTransactionsCategories.transactionsWithCategories.sumOf { it.transaction.amount })
        val availableCategories = walletTransactionsCategories.transactionsWithCategories
            .map { it.category }
            .toSet()
            .toList()
        val walletData = WalletWithTransactionsAndCategories(
            wallet = walletTransactionsCategories.wallet,
            transactionsWithCategories = if (selectedCategories.isNotEmpty()) {
                walletTransactionsCategories.transactionsWithCategories
                    .filter { selectedCategories.contains(it.category) }
                    .apply { if (this.isEmpty()) selectedCategoriesState.value = emptyList() }
            } else {
                walletTransactionsCategories.transactionsWithCategories
            }
        )

        WalletUiState.Success(
            currentBalance = currentBalance,
            availableCategories = availableCategories,
            walletWithTransactionsAndCategories = walletData,
            selectedCategories = selectedCategories,
        )
    }
        .catch { WalletUiState.Loading }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WalletUiState.Loading,
        )


    fun addToSelectedCategories(category: Category) {
        selectedCategoriesState.update { it.plus(category) }
    }

    fun removeFromSelectedCategories(category: Category) {
        selectedCategoriesState.update { it.minus(category) }
    }
}

sealed interface WalletUiState {

    data object Loading : WalletUiState

    data class Success(
        val currentBalance: BigDecimal,
        val availableCategories: List<Category?>,
        val selectedCategories: List<Category>,
        val walletWithTransactionsAndCategories: WalletWithTransactionsAndCategories,
    ) : WalletUiState
}