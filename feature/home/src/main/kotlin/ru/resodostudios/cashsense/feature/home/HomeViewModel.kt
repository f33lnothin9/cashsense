package ru.resodostudios.cashsense.feature.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.model.data.WalletWithTransactionsAndCategories
import ru.resodostudios.cashsense.feature.home.WalletsUiState.Success
import ru.resodostudios.cashsense.feature.home.navigation.HomeDestination
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val walletsRepository: WalletsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val homeDestination: HomeDestination = savedStateHandle.toRoute()

    private val shouldDisplayUndoWalletState = MutableStateFlow(false)
    private val lastRemovedWalletIdState = MutableStateFlow<String?>(null)

    val walletsUiState: StateFlow<WalletsUiState> = combine(
        walletsRepository.getWalletsWithTransactions(),
        shouldDisplayUndoWalletState,
        lastRemovedWalletIdState,
    ) { wallets, shouldDisplayUndoWallet, lastRemovedWalletId ->
        Success(
            selectedWalletId = homeDestination.walletId,
            shouldDisplayUndoWallet = shouldDisplayUndoWallet,
            walletsTransactionsCategories = wallets.filterNot { it.wallet.id == lastRemovedWalletId },
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WalletsUiState.Loading,
        )

    private fun deleteWalletWithTransactions(id: String) {
        viewModelScope.launch {
            walletsRepository.deleteWalletWithTransactions(id)
        }
    }

    fun hideWallet(id: String) {
        if (lastRemovedWalletIdState.value != null) {
            clearUndoState()
        }
        shouldDisplayUndoWalletState.value = true
        lastRemovedWalletIdState.value = id
    }

    fun undoWalletRemoval() {
        lastRemovedWalletIdState.value = null
        shouldDisplayUndoWalletState.value = false
    }

    fun clearUndoState() {
        lastRemovedWalletIdState.value?.let(::deleteWalletWithTransactions)
        undoWalletRemoval()
    }
}

sealed interface WalletsUiState {

    data object Loading : WalletsUiState

    data class Success(
        val selectedWalletId: String?,
        val shouldDisplayUndoWallet: Boolean,
        val walletsTransactionsCategories: List<WalletWithTransactionsAndCategories>,
    ) : WalletsUiState
}