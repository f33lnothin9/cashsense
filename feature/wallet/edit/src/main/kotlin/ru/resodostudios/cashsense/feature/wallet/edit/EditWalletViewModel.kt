package ru.resodostudios.cashsense.feature.wallet.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import ru.resodostudios.cashsense.core.shortcuts.ShortcutManager
import java.math.BigDecimal.ZERO
import javax.inject.Inject

@HiltViewModel
class EditWalletViewModel @Inject constructor(
    private val walletsRepository: WalletsRepository,
    private val userDataRepository: UserDataRepository,
    private val shortcutManager: ShortcutManager,
) : ViewModel() {

    private val _editWalletUiState = MutableStateFlow(EditWalletUiState())
    val editWalletUiState: StateFlow<EditWalletUiState>
        get() = _editWalletUiState.asStateFlow()

    fun saveWallet() {
        val wallet = Wallet(
            id = _editWalletUiState.value.id,
            title = _editWalletUiState.value.title,
            initialBalance = if (_editWalletUiState.value.initialBalance.isEmpty()) {
                ZERO
            } else {
                _editWalletUiState.value.initialBalance.toBigDecimal()
            },
            currency = _editWalletUiState.value.currency,
        )
        updatePrimaryWalletId(wallet.id)
        upsertWallet(wallet)
    }

    fun updateTitle(title: String) {
        _editWalletUiState.update {
            it.copy(title = title)
        }
    }

    fun updateInitialBalance(initialBalance: String) {
        _editWalletUiState.update {
            it.copy(initialBalance = initialBalance)
        }
    }

    fun updateCurrency(currency: String) {
        _editWalletUiState.update {
            it.copy(currency = currency)
        }
    }

    fun updatePrimary(isPrimary: Boolean) {
        _editWalletUiState.update {
            it.copy(isPrimary = isPrimary)
        }
    }

    private fun updatePrimaryWalletId(walletId: String) {
        viewModelScope.launch {
            if (_editWalletUiState.value.isPrimary) {
                userDataRepository.setPrimaryWalletId(_editWalletUiState.value.id)
                shortcutManager.addTransactionShortcut(walletId)
            } else if (_editWalletUiState.value.currentPrimaryWalletId == _editWalletUiState.value.id) {
                userDataRepository.setPrimaryWalletId("")
                shortcutManager.removeShortcuts()
            }
        }
    }

    fun loadWallet(id: String) {
        viewModelScope.launch {
            _editWalletUiState.value = EditWalletUiState(isLoading = true)
            val userData = userDataRepository.userData.first()
            val wallet = walletsRepository.getWallet(id).first()
            _editWalletUiState.update {
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

    private fun upsertWallet(wallet: Wallet) {
        viewModelScope.launch {
            walletsRepository.upsertWallet(wallet)
        }
    }
}

data class EditWalletUiState(
    val id: String = "",
    val title: String = "",
    val initialBalance: String = "",
    val currentPrimaryWalletId: String = "",
    val currency: String = "",
    val isPrimary: Boolean = false,
    val isLoading: Boolean = false,
)