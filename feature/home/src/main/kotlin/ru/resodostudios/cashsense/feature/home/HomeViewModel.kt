package ru.resodostudios.cashsense.feature.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.domain.GetExtendedUserWalletsUseCase
import ru.resodostudios.cashsense.core.model.data.ExtendedUserWallet
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.model.data.WalletDialogUiState
import ru.resodostudios.cashsense.core.shortcuts.ShortcutManager
import ru.resodostudios.cashsense.feature.home.WalletsUiState.Success
import ru.resodostudios.cashsense.feature.home.navigation.HomeRoute
import java.math.BigDecimal.ZERO
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val walletsRepository: WalletsRepository,
    private val userDataRepository: UserDataRepository,
    private val shortcutManager: ShortcutManager,
    getExtendedUserWallets: GetExtendedUserWalletsUseCase,
) : ViewModel() {

    private val homeDestination: HomeRoute = savedStateHandle.toRoute()

    private val selectedWalletId = savedStateHandle.getStateFlow(
        key = SELECTED_WALLET_ID_KEY,
        initialValue = homeDestination.walletId,
    )

    private val shouldDisplayUndoWalletState = MutableStateFlow(false)
    private val lastRemovedWalletIdState = MutableStateFlow<String?>(null)

    val walletsUiState: StateFlow<WalletsUiState> = combine(
        getExtendedUserWallets.invoke(),
        selectedWalletId,
        shouldDisplayUndoWalletState,
        lastRemovedWalletIdState,
    ) { extendedUserWallets, selectedWalletId, shouldDisplayUndoWallet, lastRemovedWalletId ->
        Success(
            selectedWalletId = selectedWalletId,
            shouldDisplayUndoWallet = shouldDisplayUndoWallet,
            extendedUserWallets = extendedUserWallets.filterNot { it.userWallet.id == lastRemovedWalletId },
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WalletsUiState.Loading,
        )

    private val _walletDialogUiState = MutableStateFlow(WalletDialogUiState())
    val walletDialogUiState: StateFlow<WalletDialogUiState>
        get() = _walletDialogUiState.asStateFlow()

    private fun deleteWalletWithTransactions(id: String) {
        viewModelScope.launch {
            walletsRepository.deleteWalletWithTransactions(id)
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

    private fun upsertWallet(wallet: Wallet) {
        viewModelScope.launch {
            walletsRepository.upsertWallet(wallet)
        }
    }

    fun hideWallet(id: String) {
        if (lastRemovedWalletIdState.value != null) {
            clearUndoState()
        }
        shouldDisplayUndoWalletState.value = true
        lastRemovedWalletIdState.value = id
    }

    fun undoWalletRemoval() {
        lastRemovedWalletIdState.value = null
        shouldDisplayUndoWalletState.value = false
    }

    fun clearUndoState() {
        lastRemovedWalletIdState.value?.let(::deleteWalletWithTransactions)
        undoWalletRemoval()
    }

    fun onWalletClick(walletId: String?) {
        savedStateHandle[SELECTED_WALLET_ID_KEY] = walletId
    }

    fun saveWallet() {
        val wallet = Wallet(
            id = _walletDialogUiState.value.id,
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

    fun updateCurrency(currency: String) {
        _walletDialogUiState.update {
            it.copy(currency = currency)
        }
    }

    fun updatePrimary(isPrimary: Boolean) {
        _walletDialogUiState.update {
            it.copy(isPrimary = isPrimary)
        }
    }

    fun loadWallet(id: String) {
        viewModelScope.launch {
            _walletDialogUiState.value = WalletDialogUiState(isLoading = true)
            val userData = userDataRepository.userData.first()
            val wallet = walletsRepository.getWallet(id).first()
            _walletDialogUiState.value = WalletDialogUiState(
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

sealed interface WalletsUiState {

    data object Loading : WalletsUiState

    data class Success(
        val selectedWalletId: String?,
        val shouldDisplayUndoWallet: Boolean,
        val extendedUserWallets: List<ExtendedUserWallet>,
    ) : WalletsUiState
}

private const val SELECTED_WALLET_ID_KEY = "selectedWalletId"