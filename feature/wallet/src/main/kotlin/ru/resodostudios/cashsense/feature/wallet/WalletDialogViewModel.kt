package ru.resodostudios.cashsense.feature.wallet

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
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WalletDialogViewModel @Inject constructor(
    private val walletsRepository: WalletsRepository,
) : ViewModel() {

    private val _walletDialogUiState = MutableStateFlow(WalletDialogUiState())
    val walletDialogUiState = _walletDialogUiState.asStateFlow()

    fun onWalletDialogEvent(event: WalletEvent) {
        when (event) {
            WalletEvent.Save -> {
                val wallet = Wallet(
                    id = _walletDialogUiState.value.id.ifEmpty { UUID.randomUUID().toString() },
                    title = _walletDialogUiState.value.title,
                    initialBalance = _walletDialogUiState.value.initialBalance.toBigDecimal(),
                    currency = _walletDialogUiState.value.currency,
                )
                viewModelScope.launch {
                    walletsRepository.upsertWallet(wallet)
                }
                _walletDialogUiState.update {
                    it.copy(
                        id = "",
                        title = "",
                        initialBalance = "",
                        currency = Currency.USD.name,
                        isEditing = false,
                    )
                }
            }

            WalletEvent.Delete -> {
                viewModelScope.launch {
                    walletsRepository.deleteWallet(_walletDialogUiState.value.id)
                }
            }

            is WalletEvent.UpdateId -> {
                _walletDialogUiState.update {
                    it.copy(id = event.id)
                }
                loadWallet()
            }

            is WalletEvent.UpdateTitle -> {
                _walletDialogUiState.update {
                    it.copy(title = event.title)
                }
            }

            is WalletEvent.UpdateInitialBalance -> {
                _walletDialogUiState.update {
                    it.copy(initialBalance = event.initialBalance)
                }
            }

            is WalletEvent.UpdateCurrentBalance -> {
                _walletDialogUiState.update {
                    it.copy(currentBalance = event.currentBalance)
                }
            }

            is WalletEvent.UpdateCurrency -> {
                _walletDialogUiState.update {
                    it.copy(currency = event.currency)
                }
            }
        }
    }

    private fun loadWallet() {
        viewModelScope.launch {
            walletsRepository.getWallet(_walletDialogUiState.value.id)
                .onStart { _walletDialogUiState.value = WalletDialogUiState(isEditing = true) }
                .catch { _walletDialogUiState.value = WalletDialogUiState() }
                .collect {
                    _walletDialogUiState.value = _walletDialogUiState.value.copy(
                        id = it.id,
                        title = it.title,
                        initialBalance = it.initialBalance.toString(),
                        currency = it.currency,
                        isEditing = true,
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
    val isEditing: Boolean = false,
)