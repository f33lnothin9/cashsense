package ru.resodostudios.cashsense.feature.wallets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.domain.GetExtendedUserWalletsUseCase
import ru.resodostudios.cashsense.core.model.data.ExtendedUserWallet
import javax.inject.Inject

@HiltViewModel
class WalletsViewModel @Inject constructor(
    getExtendedUserWallets: GetExtendedUserWalletsUseCase,
    userDataRepository: UserDataRepository,
    walletRepository: WalletsRepository,
) : ViewModel() {

    val walletsUiState: StateFlow<WalletsUiState> = getExtendedUserWallets.invoke()
        .map {
            if (it.isEmpty()) {
                WalletsUiState.Empty
            } else {
                WalletsUiState.Success(it)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WalletsUiState.Loading,
        )
}

sealed interface WalletsUiState {

    data object Loading : WalletsUiState

    data object Empty : WalletsUiState

    data class Success(
        val extendedUserWallets: List<ExtendedUserWallet>,
    ) : WalletsUiState
}