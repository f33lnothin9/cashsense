package ru.resodostudios.cashsense.feature.wallet.dialog

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent.Save
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent.UpdateCurrency
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent.UpdateCurrentBalance
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent.UpdateId
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent.UpdateInitialBalance
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent.UpdatePrimary
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent.UpdatePrimaryWalletId
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent.UpdateTitle
import java.math.BigDecimal.ZERO
import javax.inject.Inject
import kotlin.uuid.Uuid
import ru.resodostudios.cashsense.core.locales.R as localesR

@HiltViewModel
class WalletDialogViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
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
            UpdatePrimaryWalletId -> updatePrimaryWalletId()
            is UpdateId -> updateId(event.id)
            is UpdateTitle -> updateTitle(event.title)
            is UpdateInitialBalance -> updateInitialBalance(event.initialBalance)
            is UpdateCurrentBalance -> updateCurrentBalance(event.currentBalance)
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
        updatePrimaryWalletId()
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

    private fun updateCurrentBalance(currentBalance: String) {
        _walletDialogUiState.update {
            it.copy(currentBalance = currentBalance)
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

    private fun updatePrimaryWalletId() {
        viewModelScope.launch {
            if (_walletDialogUiState.value.isPrimary) {
                userDataRepository.setPrimaryWalletId(_walletDialogUiState.value.id)
                val shortLabel = context.getString(localesR.string.new_transaction)
                val longLabel = context.getString(localesR.string.transaction_shortcut_long_label)
                shortcutManager.addTransactionShortcut(
                    walletId = _walletDialogUiState.value.id,
                    shortLabel = shortLabel,
                    longLabel = longLabel,
                )
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
                .onStart { _walletDialogUiState.update { it.copy(isLoading = true) } }
                .collect { userData ->
                    _walletDialogUiState.update {
                        it.copy(
                            id = "",
                            title = "",
                            initialBalance = "",
                            isPrimary = false,
                            currency = userData.currency.ifEmpty { "USD" },
                        )
                    }
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
    val currentBalance: String = "",
    val currentPrimaryWalletId: String = "",
    val currency: String = "",
    val isPrimary: Boolean = false,
    val isLoading: Boolean = false,
)