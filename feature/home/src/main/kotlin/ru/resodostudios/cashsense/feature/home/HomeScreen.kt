package ru.resodostudios.cashsense.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.feature.home.WalletsUiState.Loading
import ru.resodostudios.cashsense.feature.home.WalletsUiState.Success
import ru.resodostudios.cashsense.feature.transaction.TransactionDialog
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateTransactionId
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateWalletId
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogViewModel
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletBottomSheet
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialog
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent.UpdateCurrentBalance
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent.UpdateId
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogViewModel
import ru.resodostudios.cashsense.core.ui.R as uiR

@Composable
fun HomeScreen(
    onWalletClick: (String?) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    highlightSelectedWallet: Boolean = false,
    homeViewModel: HomeViewModel = hiltViewModel(),
    walletDialogViewModel: WalletDialogViewModel = hiltViewModel(),
    transactionDialogViewModel: TransactionDialogViewModel = hiltViewModel(),
) {
    val walletsState by homeViewModel.walletsUiState.collectAsStateWithLifecycle()

    HomeScreen(
        walletsState = walletsState,
        onWalletDialogEvent = walletDialogViewModel::onWalletDialogEvent,
        onWalletClick = onWalletClick,
        onShowSnackbar = onShowSnackbar,
        onTransactionEvent = transactionDialogViewModel::onTransactionEvent,
        highlightSelectedWallet = highlightSelectedWallet,
        hideWallet = homeViewModel::hideWallet,
        undoWalletRemoval = homeViewModel::undoWalletRemoval,
        clearUndoState = homeViewModel::clearUndoState,
    )
}

@Composable
internal fun HomeScreen(
    walletsState: WalletsUiState,
    onWalletDialogEvent: (WalletDialogEvent) -> Unit,
    onWalletClick: (String?) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onTransactionEvent: (TransactionDialogEvent) -> Unit,
    highlightSelectedWallet: Boolean = false,
    hideWallet: (String) -> Unit = {},
    undoWalletRemoval: () -> Unit = {},
    clearUndoState: () -> Unit = {},
) {
    var showWalletBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showWalletDialog by rememberSaveable { mutableStateOf(false) }

    var showTransactionDialog by rememberSaveable { mutableStateOf(false) }

    when (walletsState) {
        Loading -> LoadingState(Modifier.fillMaxSize())
        is Success -> {
            val walletDeletedMessage = stringResource(R.string.feature_home_wallet_deleted)
            val undoText = stringResource(uiR.string.core_ui_undo)

            LaunchedEffect(walletsState.shouldDisplayUndoWallet) {
                if (walletsState.shouldDisplayUndoWallet) {
                    val snackBarResult = onShowSnackbar(walletDeletedMessage, undoText)
                    if (snackBarResult) {
                        undoWalletRemoval()
                    } else {
                        clearUndoState()
                    }
                }
            }
            LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
                clearUndoState()
            }

            if (walletsState.walletsTransactionsCategories.isNotEmpty()) {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(300.dp),
                    verticalItemSpacing = 16.dp,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.testTag("home:wallets"),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 88.dp,
                    ),
                ) {
                    wallets(
                        walletsState = walletsState,
                        onWalletClick = onWalletClick,
                        onTransactionCreate = {
                            onTransactionEvent(UpdateWalletId(it))
                            onTransactionEvent(UpdateTransactionId(""))
                            showTransactionDialog = true
                        },
                        onWalletMenuClick = { walletId, currentWalletBalance ->
                            onWalletDialogEvent(UpdateId(walletId))
                            onWalletDialogEvent(UpdateCurrentBalance(currentWalletBalance))
                            showWalletBottomSheet = true
                        },
                        highlightSelectedWallet = highlightSelectedWallet,
                    )
                }
            } else {
                EmptyState(
                    messageRes = R.string.feature_home_empty,
                    animationRes = R.raw.anim_wallets_empty,
                )
            }

            if (showWalletBottomSheet) {
                WalletBottomSheet(
                    onDismiss = { showWalletBottomSheet = false },
                    onEdit = { showWalletDialog = true },
                    onDelete = {
                        hideWallet(it)
                        onWalletClick(null)
                    },
                )
            }
            if (showWalletDialog) {
                WalletDialog(
                    onDismiss = { showWalletDialog = false },
                )
            }
            if (showTransactionDialog) {
                TransactionDialog(
                    onDismiss = { showTransactionDialog = false },
                )
            }
        }
    }
}

private fun LazyStaggeredGridScope.wallets(
    walletsState: WalletsUiState,
    onWalletClick: (String) -> Unit,
    onTransactionCreate: (String) -> Unit,
    onWalletMenuClick: (String, String) -> Unit,
    highlightSelectedWallet: Boolean = false,
) {
    when (walletsState) {
        Loading -> Unit
        is Success -> {
            walletsState.walletsTransactionsCategories.forEach { walletTransactionsCategories ->
                val walletId = walletTransactionsCategories.wallet.id
                val isSelected = highlightSelectedWallet && walletId == walletsState.selectedWalletId
                val isPrimary = walletId == walletsState.primaryWalletId
                item(key = walletId) {
                    WalletCard(
                        wallet = walletTransactionsCategories.wallet,
                        transactions = walletTransactionsCategories.transactionsWithCategories.map { it.transaction },
                        onWalletClick = onWalletClick,
                        onTransactionCreate = onTransactionCreate,
                        onWalletMenuClick = onWalletMenuClick,
                        modifier = Modifier.animateItem(),
                        selected = isSelected,
                        isPrimary = isPrimary,
                    )
                }
            }
        }
    }
}