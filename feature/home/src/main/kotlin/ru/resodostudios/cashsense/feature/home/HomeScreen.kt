package ru.resodostudios.cashsense.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.feature.wallets.WalletsUiState
import ru.resodostudios.cashsense.feature.wallets.WalletsViewModel
import ru.resodostudios.cashsense.feature.wallets.R as walletsR

@Composable
internal fun HomeRoute(
    walletViewModel: WalletsViewModel = hiltViewModel()
) {
    val walletsState by walletViewModel.walletsUiState.collectAsStateWithLifecycle()
    HomeScreen(
        walletsState = walletsState
    )
}

@Composable
internal fun HomeScreen(
    walletsState: WalletsUiState
) {
    when (walletsState) {
        WalletsUiState.Loading -> LoadingState()
        is WalletsUiState.Success -> if (walletsState.wallets.isNotEmpty()) {

        } else {
            EmptyState(
                messageId = walletsR.string.wallets_empty,
                animationId = walletsR.raw.anim_wallet_empty
            )
        }
    }
}