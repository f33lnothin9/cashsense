package ru.resodostudios.cashsense.feature.transfer

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
import kotlinx.datetime.Clock
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.model.data.StatusType
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.feature.transfer.navigation.TransferRoute
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import kotlin.uuid.Uuid

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val walletsRepository: WalletsRepository,
    private val transactionsRepository: TransactionsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val transferDestination: TransferRoute = savedStateHandle.toRoute()

    private val _transferState = MutableStateFlow(TransferUiState())
    val transferState: StateFlow<TransferUiState>
        get() = _transferState.asStateFlow()

    init {
        transferDestination.walletId?.let { loadTransfer(it) }
    }

    private fun loadTransfer(walletId: String) {
        viewModelScope.launch {
            _transferState.update { TransferUiState(isLoading = true) }
            val transferWallets = walletsRepository.getWalletsWithTransactionsAndCategories()
                .first()
                .map { extendedWallet ->
                    val currentBalance = extendedWallet.transactionsWithCategories
                        .sumOf { it.transaction.amount }
                        .plus(extendedWallet.wallet.initialBalance)
                    TransferWallet(
                        id = extendedWallet.wallet.id,
                        title = extendedWallet.wallet.title,
                        currentBalance = currentBalance,
                        currency = extendedWallet.wallet.currency.currencyCode,
                    )
                }
            val sendingWallet = transferWallets.first { it.id == walletId }
            val receivingWallet = if (transferWallets.size == 2) {
                transferWallets.first { it != sendingWallet }
            } else {
                TransferWallet()
            }
            val exchangeRate = if (sendingWallet.currency == receivingWallet.currency) "1" else ""
            _transferState.update {
                it.copy(
                    sendingWallet = sendingWallet,
                    receivingWallet = receivingWallet,
                    exchangeRate = exchangeRate,
                    transferWallets = transferWallets,
                    isLoading = false,
                )
            }
        }
    }

    private fun calculateConvertedAmount(amount: String, exchangeRate: String): String {
        return if (amount.isNotBlank() && exchangeRate.isNotBlank()) {
            BigDecimal(amount)
                .divide(BigDecimal(exchangeRate), 2, RoundingMode.HALF_UP)
                .toString()
        } else {
            ""
        }
    }

    fun saveTransfer() {
        val transferId = Uuid.random()
        val timestamp = Clock.System.now()
        val withdrawalAmount = BigDecimal(transferState.value.amount)
        val withdrawalTransaction = Transaction(
            id = Uuid.random().toString(),
            walletOwnerId = transferState.value.sendingWallet.id,
            description = null,
            amount = withdrawalAmount.negate(),
            timestamp = timestamp,
            status = StatusType.COMPLETED,
            ignored = true,
            transferId = transferId,
        )
        val depositTransaction = Transaction(
            id = Uuid.random().toString(),
            walletOwnerId = transferState.value.receivingWallet.id,
            description = null,
            amount = BigDecimal(transferState.value.convertedAmount),
            timestamp = timestamp,
            status = StatusType.COMPLETED,
            ignored = true,
            transferId = transferId,
        )
        val upsertWithdrawalTransactionJob = viewModelScope.launch {
            transactionsRepository.upsertTransaction(withdrawalTransaction)
        }
        val upsertDepositTransactionJob = viewModelScope.launch {
            transactionsRepository.upsertTransaction(depositTransaction)
        }
        viewModelScope.launch {
            upsertWithdrawalTransactionJob.join()
            upsertDepositTransactionJob.join()
            _transferState.update { it.copy(isTransferSaved = true) }
        }
    }

    fun updateSendingWallet(transferWallet: TransferWallet) {
        _transferState.update {
            it.copy(sendingWallet = transferWallet)
        }
        if (transferWallet.currency == _transferState.value.receivingWallet.currency) {
            _transferState.update {
                it.copy(exchangeRate = "1")
            }
        } else {
            _transferState.update {
                it.copy(exchangeRate = "")
            }
        }
    }

    fun updateReceivingWallet(transferWallet: TransferWallet) {
        _transferState.update {
            it.copy(receivingWallet = transferWallet)
        }
        if (transferWallet.currency == _transferState.value.sendingWallet.currency) {
            _transferState.update {
                it.copy(exchangeRate = "1")
            }
        } else {
            _transferState.update {
                it.copy(exchangeRate = "")
            }
        }
    }

    fun updateAmount(amount: String) {
        val convertedAmount = calculateConvertedAmount(amount, _transferState.value.exchangeRate)
        _transferState.update {
            it.copy(amount = amount, convertedAmount = convertedAmount)
        }
    }

    fun updateExchangingRate(exchangeRate: String) {
        val convertedAmount = calculateConvertedAmount(_transferState.value.amount, exchangeRate)
        _transferState.update {
            it.copy(exchangeRate = exchangeRate, convertedAmount = convertedAmount)
        }
    }

    fun updateConvertedAmount(convertedAmount: String) {
        val amount = if (convertedAmount.isNotBlank() && _transferState.value.exchangeRate.isNotBlank()) {
            (BigDecimal(convertedAmount) * BigDecimal(_transferState.value.exchangeRate)).toString()
        } else {
            ""
        }
        _transferState.update {
            it.copy(convertedAmount = convertedAmount, amount = amount)
        }
    }
}

data class TransferUiState(
    val sendingWallet: TransferWallet = TransferWallet(),
    val receivingWallet: TransferWallet = TransferWallet(),
    val amount: String = "",
    val exchangeRate: String = "",
    val convertedAmount: String = "",
    val transferWallets: List<TransferWallet> = emptyList(),
    val isLoading: Boolean = false,
    val isTransferSaved: Boolean = false,
)

data class TransferWallet(
    val id: String = "",
    val title: String = "",
    val currentBalance: BigDecimal = BigDecimal.ZERO,
    val currency: String = "",
)