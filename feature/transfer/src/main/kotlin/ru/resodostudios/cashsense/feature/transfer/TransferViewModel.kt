package ru.resodostudios.cashsense.feature.transfer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val walletsRepository: WalletsRepository,
    private val transactionsRepository: TransactionsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _transferState = MutableStateFlow(TransferUiState())
    val transferState: StateFlow<TransferUiState>
        get() = _transferState.asStateFlow()
}

data class TransferUiState(
    val fromWallet: TransferWallet = TransferWallet(),
    val toWallet: TransferWallet = TransferWallet(),
    val amount: String = "",
    val exchangeRate: String = "",
    val isLoading: Boolean = false,
)

data class TransferWallet(
    val id: String = "",
    val title: String = "",
    val currentBalance: BigDecimal = BigDecimal.ZERO,
    val currency: String = "",
)