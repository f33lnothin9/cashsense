package ru.resodostudios.cashsense.feature.wallet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.WalletWithTransactionsAndCategories
import ru.resodostudios.cashsense.feature.wallet.navigation.WalletArgs
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    walletsRepository: WalletsRepository,
) : ViewModel() {

    private val walletArgs: WalletArgs = WalletArgs(savedStateHandle)

    private val selectedCategories = MutableStateFlow<List<Category>>(emptyList())

    val walletUiState: StateFlow<WalletUiState> =
        walletsRepository.getWalletWithTransactions(walletArgs.walletId)
            .map<WalletWithTransactionsAndCategories, WalletUiState> { walletTransactionsCategories ->
                val walletData = WalletWithTransactionsAndCategories(
                    wallet = walletTransactionsCategories.wallet,
                    transactionsWithCategories = if (selectedCategories.value.isNotEmpty()) {
                        walletTransactionsCategories.transactionsWithCategories.filter { selectedCategories.value.contains(it.category) }
                    } else {
                        walletTransactionsCategories.transactionsWithCategories
                    }
                )

                WalletUiState.Success(
                    walletWithTransactionsAndCategories = walletData,
                    selectedCategories = selectedCategories.value
                )
            }
            .onStart { emit(WalletUiState.Loading) }
            .catch { emit(WalletUiState.Loading) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = WalletUiState.Loading,
            )
}

sealed interface WalletUiState {

    data object Loading : WalletUiState

    data class Success(
        val walletWithTransactionsAndCategories: WalletWithTransactionsAndCategories,
        val selectedCategories: List<Category>,
    ) : WalletUiState
}