package ru.resodostudios.cashsense.feature.wallet.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.model.data.Currency
import ru.resodostudios.cashsense.core.model.data.Wallet
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WalletDialogViewModel @Inject constructor(
    private val walletsRepository: WalletsRepository,
) : ViewModel() {

    private val _walletDialogUiState = MutableStateFlow(WalletDialogUiState())
    val walletDialogUiState = _walletDialogUiState.asStateFlow()

    fun onWalletDialogEvent(event: WalletDialogEvent) {
        when (event) {
            WalletDialogEvent.Save -> {
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
                viewModelScope.launch {
                    walletsRepository.upsertWallet(wallet)
                }
                _walletDialogUiState.update {
                    WalletDialogUiState()
                }
            }

            is WalletDialogEvent.Delete -> {
                viewModelScope.launch {
                    walletsRepository.deleteWalletWithTransactions(event.id)
                }
            }

            is WalletDialogEvent.UpdateId -> {
                _walletDialogUiState.update {
                    it.copy(id = event.id)
                }
                loadWallet()
            }

            is WalletDialogEvent.UpdateTitle -> {
                _walletDialogUiState.update {
                    it.copy(title = event.title)
                }
            }

            is WalletDialogEvent.UpdateInitialBalance -> {
                _walletDialogUiState.update {
                    it.copy(initialBalance = event.initialBalance)
                }
            }

            is WalletDialogEvent.UpdateCurrentBalance -> {
                _walletDialogUiState.update {
                    it.copy(currentBalance = event.currentBalance)
                }
            }

            is WalletDialogEvent.UpdateCurrency -> {
                _walletDialogUiState.update {
                    it.copy(currency = event.currency)
                }
            }
        }
    }

    private fun loadWallet() {
        viewModelScope.launch {
            walletsRepository.getWallet(_walletDialogUiState.value.id)
                .onStart { _walletDialogUiState.value = _walletDialogUiState.value.copy(isLoading = true) }
                .catch { _walletDialogUiState.value = WalletDialogUiState() }
                .collect {
                    _walletDialogUiState.value = _walletDialogUiState.value.copy(
                        id = it.id,
                        title = it.title,
                        initialBalance = it.initialBalance.toString(),
                        currency = it.currency,
                        isLoading = false,
                    )
                }
        }
    }
}

data class WalletDialogUiState(
    val id: String = "",
    val title: String = "",
    val initialBalance: String = "",
    val currentBalance: String = "",
    val currency: String = Currency.USD.name,
    val isLoading: Boolean = false,
)