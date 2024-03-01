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
import ru.resodostudios.cashsense.feature.transaction.TransactionEvent
import ru.resodostudios.cashsense.feature.transaction.TransactionViewModel
import ru.resodostudios.cashsense.feature.wallet.WalletBottomSheet
import ru.resodostudios.cashsense.feature.wallet.WalletCard
import ru.resodostudios.cashsense.feature.wallet.WalletDialog
import ru.resodostudios.cashsense.feature.wallet.WalletDialogViewModel
import ru.resodostudios.cashsense.feature.wallet.WalletEvent
import ru.resodostudios.cashsense.feature.wallet.R as walletR

@Composable
internal fun HomeRoute(
    onWalletClick: (String) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
    walletDialogViewModel: WalletDialogViewModel = hiltViewModel(),
    transactionViewModel: TransactionViewModel = hiltViewModel(),
) {
    val walletsState by homeViewModel.walletsUiState.collectAsStateWithLifecycle()

    HomeScreen(
        walletsState = walletsState,
        onWalletItemEvent = walletDialogViewModel::onWalletDialogEvent,
        onWalletClick = onWalletClick,
        onTransactionEvent = transactionViewModel::onTransactionEvent,
    )
}

@Composable
internal fun HomeScreen(
    walletsState: WalletsUiState,
    onWalletItemEvent: (WalletEvent) -> Unit,
    onWalletClick: (String) -> Unit,
    onTransactionEvent: (TransactionEvent) -> Unit
) {
    var showWalletBottomSheet by rememberSaveable {
        mutableStateOf(false)
    }
    var showWalletDialog by rememberSaveable {
        mutableStateOf(false)
    }

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
                    onTransactionCreate = { onTransactionEvent(TransactionEvent.UpdateWalletId(it)) },
                    onWalletMenuClick = { walletId, currentWalletBalance ->
                        onWalletItemEvent(WalletEvent.UpdateId(walletId))
                        onWalletItemEvent(WalletEvent.UpdateCurrentBalance(currentWalletBalance))
                        showWalletBottomSheet = true
                    },
                )
            }
            if (showWalletBottomSheet) {
                WalletBottomSheet(
                    onDismiss = { showWalletBottomSheet = false },
                    onEdit = { showWalletDialog = true },
                )
            }
            if (showWalletDialog) {
                WalletDialog(
                    onDismiss = { showWalletDialog = false }
                )
            }
        } else {
            EmptyState(
                messageRes = walletR.string.feature_wallet_wallets_empty,
                animationRes = walletR.raw.anim_wallet_empty,
            )
        }
    }
}

private fun LazyStaggeredGridScope.walletsWithTransactionsAndCategories(
    walletsWithTransactionsAndCategories: List<WalletWithTransactionsAndCategories>,
    onWalletClick: (String) -> Unit,
    onTransactionCreate: (String) -> Unit,
    onWalletMenuClick: (String, String) -> Unit,
) {
    items(
        items = walletsWithTransactionsAndCategories,
        key = { it.wallet.id },
        contentType = { "wallet" },
    ) { walletWithTransactionsAndCategories ->
        WalletCard(
            wallet = walletWithTransactionsAndCategories.wallet,
            transactions = walletWithTransactionsAndCategories.transactionsWithCategories.map { it.transaction },
            onWalletClick = onWalletClick,
            onTransactionCreate = onTransactionCreate,
            onWalletMenuClick = onWalletMenuClick,
        )
    }
}