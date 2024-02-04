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
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.model.data.WalletWithTransactionsAndCategories
import ru.resodostudios.cashsense.feature.wallet.WalletDialogUiState
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val walletsRepository: WalletsRepository
) : ViewModel() {

    private val _walletDialogUiState = MutableStateFlow(WalletDialogUiState())
    val walletDialogUiState = _walletDialogUiState.asStateFlow()

    val walletsUiState: StateFlow<WalletsUiState> =
        walletsRepository.getWalletsWithTransactions()
            .map<List<WalletWithTransactionsAndCategories>, WalletsUiState>(WalletsUiState::Success)
            .onStart { emit(WalletsUiState.Loading) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = WalletsUiState.Loading
            )

    fun deleteWallet(id: String) {
        viewModelScope.launch {
            walletsRepository.deleteWallet(id)
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

sealed interface WalletsUiState {

    data object Loading : WalletsUiState

    data class Success(
        val walletsWithTransactionsAndCategories: List<WalletWithTransactionsAndCategories>
    ) : WalletsUiState
}