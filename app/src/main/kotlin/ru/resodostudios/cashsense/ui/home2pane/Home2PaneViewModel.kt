package ru.resodostudios.cashsense.ui.home2pane

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import ru.resodostudios.cashsense.feature.home.navigation.WALLET_ID_ARG
import javax.inject.Inject

@HiltViewModel
class Home2PaneViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val selectedWalletId: StateFlow<String?> = savedStateHandle.getStateFlow(WALLET_ID_ARG, null)

    fun onWalletClick(walletId: String?) {
        savedStateHandle[WALLET_ID_ARG] = walletId
    }
}