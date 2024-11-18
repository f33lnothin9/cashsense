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
import ru.resodostudios.cashsense.core.ui.WalletDialogUiState
import java.math.BigDecimal.ZERO
import java.util.Currency
import javax.inject.Inject
import kotlin.uuid.Uuid

@HiltViewModel
class AddWalletViewModel @Inject constructor(
    private val walletsRepository: WalletsRepository,
    private val userDataRepository: UserDataRepository,
    private val shortcutManager: ShortcutManager,
) : ViewModel() {

    private val _walletDialogUiState = MutableStateFlow(WalletDialogUiState())
    val walletDialogUiState: StateFlow<WalletDialogUiState>
        get() = _walletDialogUiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _walletDialogUiState.value = WalletDialogUiState(isLoading = true)
            val userData = userDataRepository.userData.first()
            _walletDialogUiState.value = WalletDialogUiState(
                currency = Currency.getInstance(userData.currency.ifEmpty { "USD" }),
                isLoading = false,
            )
        }
    }

    private fun updatePrimaryWalletId(walletId: String) {
        viewModelScope.launch {
            if (_walletDialogUiState.value.isPrimary) {
                userDataRepository.setPrimaryWalletId(_walletDialogUiState.value.id)
                shortcutManager.addTransactionShortcut(walletId)
            }
        }
    }

    private fun upsertWallet(wallet: Wallet) {
        viewModelScope.launch {
            walletsRepository.upsertWallet(wallet)
        }
    }

    fun saveWallet() {
        val wallet = Wallet(
            id = Uuid.random().toString(),
            title = _walletDialogUiState.value.title,
            initialBalance = if (_walletDialogUiState.value.initialBalance.isEmpty()) {
                ZERO
            } else {
                _walletDialogUiState.value.initialBalance.toBigDecimal()
            },
            currency = _walletDialogUiState.value.currency,
        )
        updatePrimaryWalletId(wallet.id)
        upsertWallet(wallet)
        loadUserData()
    }

    fun updateTitle(title: String) {
        _walletDialogUiState.update {
            it.copy(title = title)
        }
    }

    fun updateInitialBalance(initialBalance: String) {
        _walletDialogUiState.update {
            it.copy(initialBalance = initialBalance)
        }
    }

    fun updateCurrency(currency: Currency) {
        _walletDialogUiState.update {
            it.copy(currency = currency)
        }
    }

    fun updatePrimary(isPrimary: Boolean) {
        _walletDialogUiState.update {
            it.copy(isPrimary = isPrimary)
        }
    }
}