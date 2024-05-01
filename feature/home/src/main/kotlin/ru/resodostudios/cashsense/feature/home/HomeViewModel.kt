package ru.resodostudios.cashsense.feature.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.model.data.WalletWithTransactionsAndCategories
import ru.resodostudios.cashsense.feature.home.navigation.WALLET_ID_ARG
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    walletsRepository: WalletsRepository,
) : ViewModel() {

    private val selectedWalletId: StateFlow<String?> = savedStateHandle.getStateFlow(WALLET_ID_ARG, null)

    val walletsUiState: StateFlow<WalletsUiState> = combine(
        selectedWalletId,
        walletsRepository.getWalletsWithTransactions(),
        WalletsUiState::Success,
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WalletsUiState.Loading,
        )

    fun onWalletClick(walletId: String?) {
        savedStateHandle[WALLET_ID_ARG] = walletId
    }
}

sealed interface WalletsUiState {

    data object Loading : WalletsUiState

    data class Success(
        val selectedWalletId: String?,
        val walletsTransactionsCategories: List<WalletWithTransactionsAndCategories>,
    ) : WalletsUiState
}