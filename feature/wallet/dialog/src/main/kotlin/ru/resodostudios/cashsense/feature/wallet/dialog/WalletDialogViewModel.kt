package ru.resodostudios.cashsense.feature.wallet.dialog

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
import ru.resodostudios.cashsense.core.shortcuts.ShortcutManager
import ru.resodostudios.cashsense.feature.wallet.dialog.navigation.WalletDialogRoute
import java.math.BigDecimal
import java.util.Currency
import javax.inject.Inject
import kotlin.uuid.Uuid

@HiltViewModel
class WalletDialogViewModel @Inject constructor(
    private val walletsRepository: WalletsRepository,
    private val userDataRepository: UserDataRepository,
    private val shortcutManager: ShortcutManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val walletDialogDestination: WalletDialogRoute = savedStateHandle.toRoute()

    private val _walletDialogState = MutableStateFlow(WalletDialogUiState())
    val walletDialogState: StateFlow<WalletDialogUiState>
        get() = _walletDialogState.asStateFlow()

    init {
        if (walletDialogDestination.walletId != null) {
            loadWallet(walletDialogDestination.walletId)
        } else {
            loadUserData()
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _walletDialogState.update { WalletDialogUiState(isLoading = true) }
            val userData = userDataRepository.userData.first()
            _walletDialogState.update {
                WalletDialogUiState(
                    currency = Currency.getInstance(userData.currency.ifEmpty { "USD" }),
                )
            }
        }
    }

    private fun loadWallet(id: String) {
        viewModelScope.launch {
            _walletDialogState.update {
                WalletDialogUiState(
                    id = id,
                    isLoading = true,
                )
            }
            val userData = userDataRepository.userData.first()
            val walletTransactions = walletsRepository.getWalletWithTransactionsAndCategories(id)
                .first()
            val wallet = walletTransactions.wallet
            val isCurrencyEditable = walletTransactions.transactionsWithCategories
                .map { it.transaction }
                .all { it.transferId == null }
            _walletDialogState.update {
                it.copy(
                    title = wallet.title,
                    initialBalance = wallet.initialBalance.toString(),
                    currency = wallet.currency,
                    currentPrimaryWalletId = userData.primaryWalletId,
                    isPrimary = userData.primaryWalletId == wallet.id,
                    isLoading = false,
                    isCurrencyEditable = isCurrencyEditable,
                )
            }
        }
    }

    private fun updatePrimaryWalletId(id: String) {
        viewModelScope.launch {
            if (_walletDialogState.value.isPrimary) {
                userDataRepository.setPrimaryWalletId(id)
                shortcutManager.addTransactionShortcut(id)
            } else if (_walletDialogState.value.currentPrimaryWalletId == id) {
                userDataRepository.setPrimaryWalletId("")
                shortcutManager.removeShortcuts()
            }
        }
    }

    fun saveWallet() {
        val wallet = Wallet(
            id = _walletDialogState.value.id.ifBlank { Uuid.random().toString() },
            title = _walletDialogState.value.title,
            initialBalance = if (_walletDialogState.value.initialBalance.isEmpty()) {
                BigDecimal.ZERO
            } else {
                BigDecimal(_walletDialogState.value.initialBalance)
            },
            currency = _walletDialogState.value.currency,
        )
        viewModelScope.launch {
            updatePrimaryWalletId(wallet.id)
            walletsRepository.upsertWallet(wallet)
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

    fun updateCurrency(currency: Currency) {
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

data class WalletDialogUiState(
    val id: String = "",
    val title: String = "",
    val initialBalance: String = "",
    val currentPrimaryWalletId: String = "",
    val currency: Currency = Currency.getInstance("USD"),
    val isPrimary: Boolean = false,
    val isLoading: Boolean = false,
    val isWalletSaved: Boolean = false,
    val isCurrencyEditable: Boolean = true,
)