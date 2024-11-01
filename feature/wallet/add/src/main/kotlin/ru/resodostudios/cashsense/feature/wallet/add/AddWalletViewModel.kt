package ru.resodostudios.cashsense.feature.wallet.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.shortcuts.ShortcutManager
import ru.resodostudios.cashsense.core.ui.WalletDialogUiState
import java.math.BigDecimal.ZERO
import javax.inject.Inject
import kotlin.uuid.Uuid

@HiltViewModel
class AddWalletViewModel @Inject constructor(
    private val walletsRepository: WalletsRepository,
    private val userDataRepository: UserDataRepository,
    private val shortcutManager: ShortcutManager,
) : ViewModel() {

    private val walletDialogState = MutableStateFlow(WalletDialogState())
    val walletDialogUiState: StateFlow<WalletDialogUiState> = combine(
        walletDialogState,
        userDataRepository.userData.distinctUntilChanged(),
    ) {
        walletDialogState, userData ->
        WalletDialogUiState.Success(
            id = walletDialogState.id,
            title = walletDialogState.title,
            initialBalance = walletDialogState.initialBalance,
            currency = walletDialogState.currency.ifEmpty { userData.currency.ifEmpty { "USD" } },
            currentPrimaryWalletId = userData.primaryWalletId,
            isPrimary = walletDialogState.isPrimary,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WalletDialogUiState.Loading,
        )

    fun saveWallet() {
        val wallet = Wallet(
            id = Uuid.random().toString(),
            title = walletDialogState.value.title,
            initialBalance = if (walletDialogState.value.initialBalance.isEmpty()) {
                ZERO
            } else {
                walletDialogState.value.initialBalance.toBigDecimal()
            },
            currency = walletDialogState.value.currency,
        )
        updatePrimaryWalletId(wallet.id)
        upsertWallet(wallet)
        walletDialogState.update { WalletDialogState() }
    }

    fun updateTitle(title: String) {
        walletDialogState.update {
            it.copy(title = title)
        }
    }

    fun updateInitialBalance(initialBalance: String) {
        walletDialogState.update {
            it.copy(initialBalance = initialBalance)
        }
    }

    fun updateCurrency(currency: String) {
        walletDialogState.update {
            it.copy(currency = currency)
        }
    }

    fun updatePrimary(isPrimary: Boolean) {
        walletDialogState.update {
            it.copy(isPrimary = isPrimary)
        }
    }

    private fun updatePrimaryWalletId(walletId: String) {
        viewModelScope.launch {
            if (walletDialogState.value.isPrimary) {
                userDataRepository.setPrimaryWalletId(walletDialogState.value.id)
                shortcutManager.addTransactionShortcut(walletId)
            }
        }
    }

    private fun upsertWallet(wallet: Wallet) {
        viewModelScope.launch {
            walletsRepository.upsertWallet(wallet)
        }
    }
}

data class WalletDialogState(
    val id: String = "",
    val title: String = "",
    val initialBalance: String = "",
    val currency: String = "",
    val isPrimary: Boolean = false,
)