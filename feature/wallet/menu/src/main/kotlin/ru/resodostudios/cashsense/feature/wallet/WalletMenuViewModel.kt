package ru.resodostudios.cashsense.feature.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.domain.GetUserWalletUseCase
import ru.resodostudios.cashsense.core.model.data.UserWallet
import ru.resodostudios.cashsense.core.shortcuts.ShortcutManager
import javax.inject.Inject

@HiltViewModel
class WalletMenuViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val shortcutManager: ShortcutManager,
    getUserWallet: GetUserWalletUseCase,
) : ViewModel() {

    private val walletIdState = MutableStateFlow<String?>(null)

    val walletMenuState: StateFlow<WalletMenuUiState> =
        walletIdState.flatMapLatest { walletId ->
            if (walletId == null) {
                flowOf(WalletMenuUiState.Loading)
            } else {
                getUserWallet.invoke(walletId)
                    .map(WalletMenuUiState::Success)
                    .catch { WalletMenuUiState.Loading }
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = WalletMenuUiState.Loading,
            )

    fun updateWalletId(id: String) {
        walletIdState.value = id
    }

    fun setPrimaryWalletId(id: String, isPrimary: Boolean) {
        viewModelScope.launch {
            if (isPrimary) {
                userDataRepository.setPrimaryWalletId(id)
                shortcutManager.addTransactionShortcut(id)
            } else {
                userDataRepository.setPrimaryWalletId("")
                shortcutManager.removeShortcuts()
            }
        }
    }
}

sealed interface WalletMenuUiState {

    data object Loading : WalletMenuUiState

    data class Success(
        val userWallet: UserWallet,
    ) : WalletMenuUiState
}