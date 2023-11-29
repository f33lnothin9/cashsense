package ru.resodostudios.cashsense.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.feature.transactions.TransactionDialog
import ru.resodostudios.cashsense.feature.wallets.R
import ru.resodostudios.cashsense.feature.wallets.WalletCard
import ru.resodostudios.cashsense.feature.wallets.WalletsUiState
import ru.resodostudios.cashsense.feature.wallets.WalletsViewModel

@Composable
internal fun HomeRoute(
    walletsViewModel: WalletsViewModel = hiltViewModel()
) {
    val walletsState by walletsViewModel.walletsUiState.collectAsStateWithLifecycle()
    HomeScreen(
        walletsState = walletsState,
        onEdit = { TODO() },
        onDelete = walletsViewModel::deleteWallet
    )
}

@Composable
internal fun HomeScreen(
    walletsState: WalletsUiState,
    onEdit: (Wallet) -> Unit,
    onDelete: (Wallet) -> Unit
) {
    var showNewTransactionDialog by rememberSaveable { mutableStateOf(false) }
    var walletId by rememberSaveable { mutableLongStateOf(0L) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (walletsState) {
            WalletsUiState.Loading -> LoadingState()
            is WalletsUiState.Success -> if (walletsState.wallets.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(300.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    wallets(
                        wallets = walletsState.wallets,
                        onTransactionCreate = {
                            walletId = it     
                            showNewTransactionDialog = true
                        },
                        onEdit = onEdit,
                        onDelete = onDelete
                    )
                }
            } else {
                EmptyState(
                    messageId = R.string.wallets_empty,
                    animationId = R.raw.anim_wallet_empty
                )
            }
        }
        if (showNewTransactionDialog) {
            TransactionDialog(
                onDismiss = { showNewTransactionDialog = false },
                walletId = walletId
            )
        }
    }
}

private fun LazyGridScope.wallets(
    wallets: List<Wallet>,
    onTransactionCreate: (Long) -> Unit,
    onEdit: (Wallet) -> Unit,
    onDelete: (Wallet) -> Unit
) {
    items(wallets) { wallet ->
        WalletCard(
            wallet = wallet,
            onTransactionCreate = onTransactionCreate,
            onEdit = onEdit,
            onDelete = onDelete
        )
    }
}