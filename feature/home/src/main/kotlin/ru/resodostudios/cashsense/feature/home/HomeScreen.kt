package ru.resodostudios.cashsense.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.model.data.WalletWithTransactionsAndCategories
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.feature.wallet.WalletCard
import ru.resodostudios.cashsense.feature.wallet.WalletDialog
import ru.resodostudios.cashsense.feature.wallet.WalletItemEvent
import ru.resodostudios.cashsense.feature.wallet.R as walletR

@Composable
internal fun HomeRoute(
    onWalletClick: (String) -> Unit,
    onTransactionCreate: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val walletsState by viewModel.walletsUiState.collectAsStateWithLifecycle()

    HomeScreen(
        walletsState = walletsState,
        onWalletItemEvent = viewModel::onWalletItemEvent,
        onWalletClick = onWalletClick,
        onTransactionCreate = onTransactionCreate
    )
}

@Composable
internal fun HomeScreen(
    walletsState: WalletsUiState,
    onWalletItemEvent: (WalletItemEvent) -> Unit,
    onWalletClick: (String) -> Unit,
    onTransactionCreate: (String) -> Unit
) {
    var showEditWalletDialog by rememberSaveable { mutableStateOf(false) }

    when (walletsState) {
        WalletsUiState.Loading -> LoadingState()
        is WalletsUiState.Success -> if (walletsState.walletsWithTransactionsAndCategories.isNotEmpty()) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(300.dp),
                verticalItemSpacing = 16.dp,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                walletsWithTransactionsAndCategories(
                    walletsWithTransactionsAndCategories = walletsState.walletsWithTransactionsAndCategories,
                    onWalletClick = onWalletClick,
                    onTransactionCreate = onTransactionCreate,
                    onEdit = {
                        onWalletItemEvent(WalletItemEvent.UpdateId(it))
                        showEditWalletDialog = true
                    },
                    onDelete = { onWalletItemEvent(WalletItemEvent.Delete(it)) }
                )
            }
            if (showEditWalletDialog) {
                WalletDialog(
                    onDismiss = { showEditWalletDialog = false }
                )
            }
        } else {
            EmptyState(
                messageRes = walletR.string.feature_wallet_wallets_empty,
                animationRes = walletR.raw.anim_wallet_empty
            )
        }
    }
}

private fun LazyStaggeredGridScope.walletsWithTransactionsAndCategories(
    walletsWithTransactionsAndCategories: List<WalletWithTransactionsAndCategories>,
    onWalletClick: (String) -> Unit,
    onTransactionCreate: (String) -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    items(
        items = walletsWithTransactionsAndCategories,
        key = { it.wallet.id },
        contentType = { "walletItem" }
    ) { walletWithTransactionsAndCategories ->
        WalletCard(
            wallet = walletWithTransactionsAndCategories.wallet,
            transactions = walletWithTransactionsAndCategories.transactionsWithCategories.map { it.transaction },
            onWalletClick = onWalletClick,
            onTransactionCreate = onTransactionCreate,
            onEdit = onEdit,
            onDelete = onDelete
        )
    }
}