package ru.resodostudios.cashsense.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.model.data.Currency
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.model.data.WalletWithTransactionsAndCategories
import ru.resodostudios.cashsense.feature.wallet.WalletItemEvent
import ru.resodostudios.cashsense.feature.wallet.WalletItemUiState
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val walletsRepository: WalletsRepository
) : ViewModel() {

    private val _walletItemUiState = MutableStateFlow(WalletItemUiState())
    val walletItemUiState = _walletItemUiState.asStateFlow()

    val walletsUiState: StateFlow<WalletsUiState> =
        walletsRepository.getWalletsWithTransactions()
            .map<List<WalletWithTransactionsAndCategories>, WalletsUiState>(WalletsUiState::Success)
            .onStart { emit(WalletsUiState.Loading) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = WalletsUiState.Loading
            )

    fun onWalletItemEvent(event: WalletItemEvent) {
        when (event) {
            WalletItemEvent.Confirm -> {
                val wallet = Wallet(
                    id = _walletItemUiState.value.id.ifEmpty { UUID.randomUUID().toString() },
                    title = _walletItemUiState.value.title,
                    initialBalance = _walletItemUiState.value.initialBalance.toBigDecimal(),
                    currency = _walletItemUiState.value.currency
                )
                viewModelScope.launch {
                    walletsRepository.upsertWallet(wallet)
                }
                _walletItemUiState.update {
                    it.copy(
                        id = "",
                        title = "",
                        initialBalance = "",
                        currency = Currency.USD.name,
                        isEditing = false
                    )
                }
            }

            is WalletItemEvent.Delete -> {
                viewModelScope.launch {
                    walletsRepository.deleteWallet(event.id)
                }
            }

            is WalletItemEvent.UpdateId -> {
                _walletItemUiState.update {
                    it.copy(id = event.id)
                }
                loadWallet()
            }

            is WalletItemEvent.UpdateTitle -> {
                _walletItemUiState.update {
                    it.copy(title = event.title)
                }
            }

            is WalletItemEvent.UpdateInitialBalance -> {
                _walletItemUiState.update {
                    it.copy(initialBalance = event.initialBalance)
                }
            }

            is WalletItemEvent.UpdateCurrency -> {
                _walletItemUiState.update {
                    it.copy(currency = event.currency)
                }
            }
        }
    }

    private fun loadWallet() {
        viewModelScope.launch {
            walletsRepository.getWallet(_walletItemUiState.value.id)
                .onEach {
                    _walletItemUiState.emit(
                        WalletItemUiState(
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

sealed interface WalletsUiState {

    data object Loading : WalletsUiState

    data class Success(
        val walletsWithTransactionsAndCategories: List<WalletWithTransactionsAndCategories>
    ) : WalletsUiState
}