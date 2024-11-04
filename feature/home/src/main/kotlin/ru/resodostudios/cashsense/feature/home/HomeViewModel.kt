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
import ru.resodostudios.cashsense.core.domain.GetExtendedUserWalletsUseCase
import ru.resodostudios.cashsense.core.model.data.ExtendedUserWallet
import ru.resodostudios.cashsense.feature.home.WalletsUiState.Success
import ru.resodostudios.cashsense.feature.home.navigation.HomeRoute
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val walletsRepository: WalletsRepository,
    getExtendedUserWallets: GetExtendedUserWalletsUseCase,
) : ViewModel() {

    private val homeDestination: HomeRoute = savedStateHandle.toRoute()

    private val selectedWalletId = savedStateHandle.getStateFlow(
        key = SELECTED_WALLET_ID_KEY,
        initialValue = homeDestination.walletId,
    )

    private val shouldDisplayUndoWalletState = MutableStateFlow(false)
    private val lastRemovedWalletIdState = MutableStateFlow<String?>(null)

    val walletsUiState: StateFlow<WalletsUiState> = combine(
        getExtendedUserWallets.invoke(),
        selectedWalletId,
        shouldDisplayUndoWalletState,
        lastRemovedWalletIdState,
    ) { extendedUserWallets, selectedWalletId, shouldDisplayUndoWallet, lastRemovedWalletId ->
        Success(
            selectedWalletId = selectedWalletId,
            shouldDisplayUndoWallet = shouldDisplayUndoWallet,
            extendedUserWallets = extendedUserWallets.filterNot { it.userWallet.id == lastRemovedWalletId },
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

    fun onWalletClick(walletId: String?) {
        savedStateHandle[SELECTED_WALLET_ID_KEY] = walletId
    }
}

sealed interface WalletsUiState {

    data object Loading : WalletsUiState

    data class Success(
        val selectedWalletId: String?,
        val shouldDisplayUndoWallet: Boolean,
        val extendedUserWallets: List<ExtendedUserWallet>,
    ) : WalletsUiState
}

private const val SELECTED_WALLET_ID_KEY = "selectedWalletId"