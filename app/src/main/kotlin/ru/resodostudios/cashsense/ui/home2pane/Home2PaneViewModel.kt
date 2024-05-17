package ru.resodostudios.cashsense.ui.home2pane

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import ru.resodostudios.cashsense.feature.home.navigation.HomeDestination
import ru.resodostudios.cashsense.feature.home.navigation.WALLET_ID_KEY
import javax.inject.Inject

@HiltViewModel
class Home2PaneViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val destination = savedStateHandle.toRoute<HomeDestination>()
    val selectedWalletId: StateFlow<String?> = savedStateHandle.getStateFlow(
        key = WALLET_ID_KEY,
        initialValue = destination.initialWalletId,
    )

    fun onWalletClick(walletId: String?) {
        savedStateHandle[WALLET_ID_KEY] = walletId
    }
}
