package ru.resodostudios.cashsense.feature.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.feature.wallets.WalletCard
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
            LazyVerticalGrid(
                columns = GridCells.Adaptive(300.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                wallets(
                    wallets = walletsState.wallets,
                    onEdit = { TODO() }
                )
            }
        } else {
            EmptyState(
                messageId = walletsR.string.wallets_empty,
                animationId = walletsR.raw.anim_wallet_empty
            )
        }
    }
}

private fun LazyGridScope.wallets(
    wallets: List<Wallet>,
    onEdit: (Wallet) -> Unit
) {
    items(wallets) { wallet ->
        WalletCard(
            wallet = wallet,
            onEdit = onEdit
        )
    }
}