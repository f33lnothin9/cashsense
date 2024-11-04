package ru.resodostudios.cashsense.feature.wallet.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.model.data.WalletDialogUiState
import ru.resodostudios.cashsense.core.shortcuts.ShortcutManager
import ru.resodostudios.cashsense.feature.wallet.edit.navigation.EditWalletRoute
import java.math.BigDecimal.ZERO
import javax.inject.Inject

@HiltViewModel
class EditWalletViewModel @Inject constructor(
    private val walletsRepository: WalletsRepository,
    private val userDataRepository: UserDataRepository,
    private val shortcutManager: ShortcutManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val editWalletDestination: EditWalletRoute = savedStateHandle.toRoute()

    private val _walletDialogState = MutableStateFlow(WalletDialogUiState())
    val walletDialogState: StateFlow<WalletDialogUiState>
        get() = _walletDialogState.asStateFlow()

    init {
        editWalletDestination.walletId?.let { loadWallet(it) }
    }

    private fun updatePrimaryWalletId(walletId: String) {
        viewModelScope.launch {
            if (_walletDialogState.value.isPrimary) {
                userDataRepository.setPrimaryWalletId(_walletDialogState.value.id)
                shortcutManager.addTransactionShortcut(walletId)
            } else if (_walletDialogState.value.currentPrimaryWalletId == _walletDialogState.value.id) {
                userDataRepository.setPrimaryWalletId("")
                shortcutManager.removeShortcuts()
            }
        }
    }

    private fun upsertWallet(wallet: Wallet) {
        viewModelScope.launch {
            walletsRepository.upsertWallet(wallet)
        }
    }

    private fun loadWallet(id: String) {
        viewModelScope.launch {
            _walletDialogState.update { WalletDialogUiState(isLoading = true) }
            val userData = userDataRepository.userData.first()
            val wallet = walletsRepository.getWallet(id).first()
            _walletDialogState.update {
                it.copy(
                    id = wallet.id,
                    title = wallet.title,
                    initialBalance = wallet.initialBalance.toString(),
                    currency = wallet.currency,
                    currentPrimaryWalletId = userData.primaryWalletId,
                    isPrimary = userData.primaryWalletId == wallet.id,
                    isLoading = false,
                )
            }
        }
    }

    fun saveWallet() {
        val wallet = Wallet(
            id = _walletDialogState.value.id,
            title = _walletDialogState.value.title,
            initialBalance = if (_walletDialogState.value.initialBalance.isEmpty()) {
                ZERO
            } else {
                _walletDialogState.value.initialBalance.toBigDecimal()
            },
            currency = _walletDialogState.value.currency,
        )
        updatePrimaryWalletId(wallet.id)
        upsertWallet(wallet)
    }

    fun updateTitle(title: String) {
        _walletDialogState.update {
            it.copy(title = title)
        }
    }

    fun updateInitialBalance(initialBalance: String) {
        _walletDialogState.update {
            it.copy(initialBalance = initialBalance)
        }
    }

    fun updateCurrency(currency: String) {
        _walletDialogState.update {
            it.copy(currency = currency)
        }
    }

    fun updatePrimary(isPrimary: Boolean) {
        _walletDialogState.update {
            it.copy(isPrimary = isPrimary)
        }
    }
}