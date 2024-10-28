package ru.resodostudios.cashsense.feature.wallet.add

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
import kotlin.uuid.Uuid

@HiltViewModel
class AddWalletViewModel @Inject constructor(
    private val walletsRepository: WalletsRepository,
    private val userDataRepository: UserDataRepository,
    private val shortcutManager: ShortcutManager,
) : ViewModel() {

    private val _addWalletUiState = MutableStateFlow(WalletDialogUiState())
    val addWalletUiState: StateFlow<WalletDialogUiState>
        get() = _addWalletUiState.asStateFlow()

    init {
        loadUserData()
    }

    fun saveWallet() {
        val wallet = Wallet(
            id = Uuid.random().toString(),
            title = _addWalletUiState.value.title,
            initialBalance = if (_addWalletUiState.value.initialBalance.isEmpty()) {
                ZERO
            } else {
                _addWalletUiState.value.initialBalance.toBigDecimal()
            },
            currency = _addWalletUiState.value.currency,
        )
        _addWalletUiState.update {
            it.copy(id = wallet.id)
        }
        updatePrimaryWalletId(wallet.id)
        upsertWallet(wallet)
        loadUserData()
    }

    fun updateTitle(title: String) {
        _addWalletUiState.update {
            it.copy(title = title)
        }
    }

    fun updateInitialBalance(initialBalance: String) {
        _addWalletUiState.update {
            it.copy(initialBalance = initialBalance)
        }
    }

    fun updateCurrency(currency: String) {
        _addWalletUiState.update {
            it.copy(currency = currency)
        }
    }

    fun updatePrimary(isPrimary: Boolean) {
        _addWalletUiState.update {
            it.copy(isPrimary = isPrimary)
        }
    }

    private fun updatePrimaryWalletId(walletId: String) {
        viewModelScope.launch {
            if (_addWalletUiState.value.isPrimary) {
                userDataRepository.setPrimaryWalletId(_addWalletUiState.value.id)
                shortcutManager.addTransactionShortcut(walletId)
            }
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _addWalletUiState.value = WalletDialogUiState(isLoading = true)
            val userData = userDataRepository.userData.first()
            _addWalletUiState.value = WalletDialogUiState(
                currency = userData.currency.ifEmpty { "USD" },
                isLoading = false,
            )
        }
    }

    private fun upsertWallet(wallet: Wallet) {
        viewModelScope.launch {
            walletsRepository.upsertWallet(wallet)
        }
    }
}

data class WalletDialogUiState(
    val id: String = "",
    val title: String = "",
    val initialBalance: String = "",
    val currency: String = "",
    val isPrimary: Boolean = false,
    val isLoading: Boolean = false,
)