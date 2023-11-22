package ru.resodostudios.cashsense.feature.wallets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.model.data.Wallet
import javax.inject.Inject

@HiltViewModel
class WalletsViewModel @Inject constructor(
    private val walletsRepository: WalletsRepository
) : ViewModel() {

    val walletsUiState: StateFlow<WalletsUiState> =
        walletsRepository.getWallets()
            .map<List<Wallet>, WalletsUiState>(WalletsUiState::Success)
            .onStart { emit(WalletsUiState.Loading) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = WalletsUiState.Loading
            )
}

sealed interface WalletsUiState {

    data object Loading : WalletsUiState

    data class Success(
        val wallets: List<Wallet>
    ) : WalletsUiState
}