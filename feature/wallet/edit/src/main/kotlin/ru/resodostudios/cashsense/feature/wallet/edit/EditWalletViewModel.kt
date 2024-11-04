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
import java.math.BigDecimal
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
                BigDecimal.ZERO
            } else {
                BigDecimal(_walletDialogState.value.initialBalance)
            },
            currency = _walletDialogState.value.currency,
        )
        val updatePrimaryIdJob = viewModelScope.launch {
            if (_walletDialogState.value.isPrimary) {
                userDataRepository.setPrimaryWalletId(wallet.id)
                shortcutManager.addTransactionShortcut(wallet.id)
            } else if (_walletDialogState.value.currentPrimaryWalletId == wallet.id) {
                userDataRepository.setPrimaryWalletId("")
                shortcutManager.removeShortcuts()
            }
        }
        val upsertWalletJob = viewModelScope.launch {
            walletsRepository.upsertWallet(wallet)
        }
        viewModelScope.launch {
            updatePrimaryIdJob.join()
            upsertWalletJob.join()
            _walletDialogState.update { it.copy(isWalletSaved = true) }
        }
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