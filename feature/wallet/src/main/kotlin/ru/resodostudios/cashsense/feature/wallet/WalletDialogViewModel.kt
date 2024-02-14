package ru.resodostudios.cashsense.feature.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.model.data.Currency
import ru.resodostudios.cashsense.core.model.data.Wallet
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WalletDialogViewModel @Inject constructor(
    private val walletsRepository: WalletsRepository
) : ViewModel() {

    private val _walletDialogUiState = MutableStateFlow(WalletDialogUiState())
    val walletDialogUiState = _walletDialogUiState.asStateFlow()

    fun onWalletDialogEvent(event: WalletDialogEvent) {
        when (event) {
            WalletDialogEvent.Confirm -> {
                val wallet = Wallet(
                    id = _walletDialogUiState.value.id.ifEmpty { UUID.randomUUID().toString() },
                    title = _walletDialogUiState.value.title,
                    initialBalance = _walletDialogUiState.value.initialBalance.toBigDecimal(),
                    currency = _walletDialogUiState.value.currency
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
                        isEditing = false
                    )
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
                .onEach {
                    _walletDialogUiState.emit(
                        WalletDialogUiState(
                            id = it.id,
                            title = it.title,
                            initialBalance = it.initialBalance.toString(),
                            currency = it.currency,
                            isEditing = true,
                        )
                    )
                }
                .collect()
        }
    }
}

data class WalletDialogUiState(
    val id: String = "",
    val title: String = "",
    val initialBalance: String = "",
    val currency: String = Currency.USD.name,
    val isEditing: Boolean = false,
)