package ru.resodostudios.cashsense.feature.wallet.dialog

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.data.util.ShortcutManager
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent.Save
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent.UpdateCurrency
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent.UpdateCurrentBalance
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent.UpdateId
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent.UpdateInitialBalance
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent.UpdatePrimary
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent.UpdatePrimaryWalletId
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent.UpdateTitle
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject
import ru.resodostudios.cashsense.feature.transaction.R as transactionR

@HiltViewModel
class WalletDialogViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val walletsRepository: WalletsRepository,
    private val userDataRepository: UserDataRepository,
    private val shortcutManager: ShortcutManager,
) : ViewModel() {

    private val _walletDialogUiState = MutableStateFlow(WalletDialogUiState())
    val walletDialogUiState = _walletDialogUiState.asStateFlow()

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
            id = _walletDialogUiState.value.id.ifEmpty { UUID.randomUUID().toString() },
            title = _walletDialogUiState.value.title,
            initialBalance = if (_walletDialogUiState.value.initialBalance.isEmpty()) {
                BigDecimal.ZERO
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
        _walletDialogUiState.update {
            WalletDialogUiState()
        }
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
                val shortLabel = context.getString(transactionR.string.feature_transaction_new_transaction)
                val longLabel = context.getString(transactionR.string.feature_transaction_shortcut_long_label)
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
            combine(
                userDataRepository.userData,
                walletsRepository.getWallet(_walletDialogUiState.value.id),
            ) { userData, wallet ->
                _walletDialogUiState.value.copy(
                    id = wallet.id,
                    title = wallet.title,
                    initialBalance = wallet.initialBalance.toString(),
                    currency = wallet.currency,
                    currentPrimaryWalletId = userData.primaryWalletId,
                    isPrimary = userData.primaryWalletId == wallet.id,
                    isLoading = false,
                )
            }
                .onStart { _walletDialogUiState.value = _walletDialogUiState.value.copy(isLoading = true) }
                .catch { _walletDialogUiState.value = WalletDialogUiState() }
                .collect { _walletDialogUiState.value = it }
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
    val currency: String = "USD",
    val isPrimary: Boolean = false,
    val isLoading: Boolean = false,
)