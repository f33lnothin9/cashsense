package ru.resodostudios.cashsense.feature.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.model.data.WalletWithTransactionsAndCategories
import ru.resodostudios.cashsense.feature.home.WalletsUiState.Success
import ru.resodostudios.cashsense.feature.home.navigation.HomeDestination
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    walletsRepository: WalletsRepository,
) : ViewModel() {

    private val homeDestination: HomeDestination = savedStateHandle.toRoute()

    val walletsUiState: StateFlow<WalletsUiState> = walletsRepository.getWalletsWithTransactions()
        .map { wallets ->
            Success(homeDestination.walletId, wallets)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WalletsUiState.Loading,
        )
}

sealed interface WalletsUiState {

    data object Loading : WalletsUiState

    data class Success(
        val selectedWalletId: String?,
        val walletsTransactionsCategories: List<WalletWithTransactionsAndCategories>,
    ) : WalletsUiState
}