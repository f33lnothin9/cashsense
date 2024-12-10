package ru.resodostudios.cashsense.feature.wallet.dialog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.network.di.ApplicationScope
import ru.resodostudios.cashsense.feature.wallet.dialog.navigation.WalletDialogRoute
import java.util.Currency
import javax.inject.Inject

@HiltViewModel
class WalletDialogViewModel @Inject constructor(
    private val walletsRepository: WalletsRepository,
    private val userDataRepository: UserDataRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationScope private val appScope: CoroutineScope,
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

    fun saveWallet(wallet: Wallet, isPrimary: Boolean) {
        appScope.launch {
            walletsRepository.upsertWallet(wallet)
            userDataRepository.setPrimaryWalletId(wallet.id, isPrimary)
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
    val isCurrencyEditable: Boolean = true,
)