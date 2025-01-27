package ru.resodostudios.cashsense.ui.home2pane

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.domain.GetExtendedUserWalletUseCase
import ru.resodostudios.cashsense.core.model.data.ExtendedUserWallet
import ru.resodostudios.cashsense.core.model.data.TransactionCategoryCrossRef
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.util.Constants.WALLET_ID_KEY
import ru.resodostudios.cashsense.feature.home.navigation.HomeRoute
import javax.inject.Inject

@HiltViewModel
class Home2PaneViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val walletsRepository: WalletsRepository,
    private val transactionsRepository: TransactionsRepository,
    private val userDataRepository: UserDataRepository,
    private val getExtendedUserWallet: GetExtendedUserWalletUseCase,
) : ViewModel() {

    private val homeDestination: HomeRoute = savedStateHandle.toRoute()

    val selectedWalletId: StateFlow<String?> = savedStateHandle.getStateFlow(
        key = WALLET_ID_KEY,
        initialValue = homeDestination.walletId,
    )

    private val lastRemovedWalletState = MutableStateFlow<ExtendedUserWallet?>(null)
    private val _shouldDisplayUndoWalletState = MutableStateFlow(false)
    val shouldDisplayUndoWalletState = _shouldDisplayUndoWalletState.asStateFlow()

    fun onWalletClick(walletId: String?) {
        savedStateHandle[WALLET_ID_KEY] = walletId
    }

    fun deleteWallet(walletId: String) {
        viewModelScope.launch {
            lastRemovedWalletState.value = getExtendedUserWallet.invoke(walletId).first()
            _shouldDisplayUndoWalletState.value = true
            walletsRepository.deleteWalletWithTransactions(walletId)
        }
    }

    fun undoWalletRemoval() {
        viewModelScope.launch {
            lastRemovedWalletState.value?.let {
                val wallet = Wallet(
                    id = it.userWallet.id,
                    title = it.userWallet.title,
                    initialBalance = it.userWallet.initialBalance,
                    currency = it.userWallet.currency,
                )
                walletsRepository.upsertWallet(wallet)
                if (it.userWallet.isPrimary) {
                    userDataRepository.setPrimaryWallet(wallet.id, true)
                }
                it.transactionsWithCategories.forEach { transactionWithCategory ->
                    transactionsRepository.upsertTransaction(transactionWithCategory.transaction)
                    if (transactionWithCategory.category != null) {
                        transactionWithCategory.category?.id?.let { categoryId ->
                            val crossRef = TransactionCategoryCrossRef(
                                transactionId = transactionWithCategory.transaction.id,
                                categoryId = categoryId,
                            )
                            transactionsRepository.upsertTransactionCategoryCrossRef(crossRef)
                        }
                    }
                }
            }
            clearUndoState()
        }
    }

    fun clearUndoState() {
        _shouldDisplayUndoWalletState.value = false
        lastRemovedWalletState.value = null
    }
}
