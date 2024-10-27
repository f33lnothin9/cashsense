package ru.resodostudios.cashsense.feature.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.shortcuts.ShortcutManager
import ru.resodostudios.cashsense.feature.wallet.WalletDialogEvent.Save
import ru.resodostudios.cashsense.feature.wallet.WalletDialogEvent.UpdateCurrency
import ru.resodostudios.cashsense.feature.wallet.WalletDialogEvent.UpdateId
import ru.resodostudios.cashsense.feature.wallet.WalletDialogEvent.UpdateInitialBalance
import ru.resodostudios.cashsense.feature.wallet.WalletDialogEvent.UpdatePrimary
import ru.resodostudios.cashsense.feature.wallet.WalletDialogEvent.UpdateTitle
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import javax.inject.Inject
import kotlin.uuid.Uuid

@HiltViewModel
class WalletDialogViewModel @Inject constructor(
    private val walletsRepository: WalletsRepository,
    private val userDataRepository: UserDataRepository,
    private val shortcutManager: ShortcutManager,
) : ViewModel() {

    private val _walletDialogUiState = MutableStateFlow(WalletDialogUiState())
    val walletDialogUiState: StateFlow<WalletDialogUiState>
        get() = _walletDialogUiState.asStateFlow()

    init {
        if (_walletDialogUiState.value.id.isEmpty()) clearWalletDialogState()
    }

    fun onWalletDialogEvent(event: WalletDialogEvent) {
        when (event) {
            Save -> saveWallet()
            is UpdateId -> updateId(event.id)
            is UpdateTitle -> updateTitle(event.title)
            is UpdateInitialBalance -> updateInitialBalance(event.initialBalance)
            is UpdateCurrency -> updateCurrency(event.currency)
            is UpdatePrimary -> updatePrimary(event.isPrimary)
        }
    }

    private fun saveWallet() {
        val wallet = Wallet(
            id = _walletDialogUiState.value.id.ifEmpty { Uuid.random().toString() },
            title = _walletDialogUiState.value.title,
            initialBalance = if (_walletDialogUiState.value.initialBalance.isEmpty()) {
                ZERO
            } else {
                _walletDialogUiState.value.initialBalance.toBigDecimal()
            },
            currency = _walletDialogUiState.value.currency,
        )
        _walletDialogUiState.update {
            it.copy(id = wallet.id)
        }
        updatePrimaryWalletId(wallet.id)
        upsertWallet(wallet)
        clearWalletDialogState()
    }

    private fun updateId(id: String) {
        _walletDialogUiState.update {
            it.copy(id = id)
        }
        loadWallet()
    }

    private fun updateTitle(title: String) {
        _walletDialogUiState.update {
            it.copy(title = title)
        }
    }

    private fun updateInitialBalance(initialBalance: String) {
        _walletDialogUiState.update {
            it.copy(initialBalance = initialBalance)
        }
    }

    private fun updateCurrency(currency: String) {
        _walletDialogUiState.update {
            it.copy(currency = currency)
        }
    }

    private fun updatePrimary(isPrimary: Boolean) {
        _walletDialogUiState.update {
            it.copy(isPrimary = isPrimary)
        }
    }

    private fun updatePrimaryWalletId(walletId: String) {
        viewModelScope.launch {
            if (_walletDialogUiState.value.isPrimary) {
                userDataRepository.setPrimaryWalletId(_walletDialogUiState.value.id)
                shortcutManager.addTransactionShortcut(walletId)
            } else if (_walletDialogUiState.value.currentPrimaryWalletId == _walletDialogUiState.value.id) {
                userDataRepository.setPrimaryWalletId("")
                shortcutManager.removeShortcuts()
            }
        }
    }

    private fun loadWallet() {
        viewModelScope.launch {
            _walletDialogUiState.update { it.copy(isLoading = true) }
            val userData = userDataRepository.userData.first()
            val wallet = walletsRepository.getWallet(_walletDialogUiState.value.id).first()
            _walletDialogUiState.update {
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

    private fun clearWalletDialogState() {
        viewModelScope.launch {
            userDataRepository.userData
                .onStart {
                    _walletDialogUiState.value = _walletDialogUiState.value.copy(isLoading = true)
                }
                .collect { userData ->
                    _walletDialogUiState.value = _walletDialogUiState.value.copy(
                        id = "",
                        title = "",
                        initialBalance = "",
                        isPrimary = false,
                        currency = userData.currency.ifEmpty { "USD" },
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

data class WalletDialogUiState(
    val id: String = "",
    val title: String = "",
    val initialBalance: String = "",
    val currentBalance: BigDecimal = ZERO,
    val currentPrimaryWalletId: String = "",
    val currency: String = "",
    val isPrimary: Boolean = false,
    val isLoading: Boolean = false,
)