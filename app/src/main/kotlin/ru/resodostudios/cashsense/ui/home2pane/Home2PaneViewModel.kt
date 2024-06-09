package ru.resodostudios.cashsense.ui.home2pane

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import ru.resodostudios.cashsense.feature.home.navigation.HomeRoute
import ru.resodostudios.cashsense.feature.home.navigation.OPEN_TRANSACTION_DIALOG_KEY
import ru.resodostudios.cashsense.feature.home.navigation.WALLET_ID_KEY
import javax.inject.Inject

@HiltViewModel
class Home2PaneViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<HomeRoute>()

    val selectedWalletId: StateFlow<String?> = savedStateHandle.getStateFlow(
        key = WALLET_ID_KEY,
        initialValue = route.initialWalletId,
    )
    val openTransactionDialog: StateFlow<Boolean> = savedStateHandle.getStateFlow(
        key = OPEN_TRANSACTION_DIALOG_KEY,
        initialValue = route.openTransactionDialog,
    )

    fun onWalletClick(walletId: String?) {
        savedStateHandle[WALLET_ID_KEY] = walletId
    }

    fun onTransactionDialogDismiss() {
        savedStateHandle[OPEN_TRANSACTION_DIALOG_KEY] = false
    }
}
