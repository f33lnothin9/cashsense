package ru.resodostudios.cashsense.feature.wallet.dialog

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.domain.GetUserWalletUseCase
import ru.resodostudios.cashsense.core.locales.R
import ru.resodostudios.cashsense.core.model.data.UserWallet
import ru.resodostudios.cashsense.core.shortcuts.ShortcutManager
import javax.inject.Inject

@HiltViewModel
class WalletMenuViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
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
                val shortLabel = context.getString(R.string.new_transaction)
                val longLabel = context.getString(R.string.transaction_shortcut_long_label)
                shortcutManager.addTransactionShortcut(
                    walletId = id,
                    shortLabel = shortLabel,
                    longLabel = longLabel,
                )
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