package ru.resodostudios.cashsense.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
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
import ru.resodostudios.cashsense.feature.wallet.menu.WalletMenu
import ru.resodostudios.cashsense.feature.wallet.menu.WalletMenuViewModel
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
fun HomeScreen(
    onWalletClick: (String?) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    highlightSelectedWallet: Boolean = false,
    onTransactionCreate: (String) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
    walletMenuViewModel: WalletMenuViewModel = hiltViewModel(),
) {
    val walletsState by homeViewModel.walletsUiState.collectAsStateWithLifecycle()

    HomeScreen(
        walletsState = walletsState,
        onWalletClick = {
            homeViewModel.onWalletClick(it)
            onWalletClick(it)
        },
        onWalletMenuClick = walletMenuViewModel::updateWalletId,
        onShowSnackbar = onShowSnackbar,
        onTransactionCreate = onTransactionCreate,
        highlightSelectedWallet = highlightSelectedWallet,
        hideWallet = homeViewModel::hideWallet,
        undoWalletRemoval = homeViewModel::undoWalletRemoval,
        clearUndoState = homeViewModel::clearUndoState,
    )
}

@Composable
internal fun HomeScreen(
    walletsState: WalletsUiState,
    onWalletClick: (String?) -> Unit,
    onWalletMenuClick: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onTransactionCreate: (String) -> Unit,
    highlightSelectedWallet: Boolean = false,
    hideWallet: (String) -> Unit = {},
    undoWalletRemoval: () -> Unit = {},
    clearUndoState: () -> Unit = {},
) {
    var showWalletMenu by rememberSaveable { mutableStateOf(false) }

    when (walletsState) {
        Loading -> LoadingState(Modifier.fillMaxSize())
        is Success -> {
            val walletDeletedMessage = stringResource(localesR.string.wallet_deleted)
            val undoText = stringResource(localesR.string.undo)

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

            if (walletsState.extendedUserWallets.isNotEmpty()) {
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
                        onTransactionCreate = onTransactionCreate,
                        onWalletMenuClick = { walletId ->
                            onWalletMenuClick(walletId)
                            showWalletMenu = true
                        },
                        highlightSelectedWallet = highlightSelectedWallet,
                    )
                }
            } else {
                EmptyState(
                    messageRes = localesR.string.home_empty,
                    animationRes = R.raw.anim_wallets_empty,
                )
            }

            if (showWalletMenu) {
                WalletMenu(
                    onDismiss = { showWalletMenu = false },
                    onEdit = {
                        // TODO
                    },
                    onDelete = {
                        hideWallet(it)
                        onWalletClick(null)
                    },
                )
            }
        }
    }
}

private fun LazyStaggeredGridScope.wallets(
    walletsState: WalletsUiState,
    onWalletClick: (String) -> Unit,
    onTransactionCreate: (String) -> Unit,
    onWalletMenuClick: (String) -> Unit,
    highlightSelectedWallet: Boolean = false,
) {
    when (walletsState) {
        Loading -> Unit
        is Success -> {
            items(
                items = walletsState.extendedUserWallets,
                key = { it.userWallet.id },
                contentType = { "walletCard" },
            ) { walletData ->
                val selected = highlightSelectedWallet && walletData.userWallet.id == walletsState.selectedWalletId
                WalletCard(
                    userWallet = walletData.userWallet,
                    transactions = walletData.transactionsWithCategories.map { it.transaction },
                    onWalletClick = onWalletClick,
                    onTransactionCreate = onTransactionCreate,
                    onWalletMenuClick = onWalletMenuClick,
                    modifier = Modifier.animateItem(),
                    selected = selected,
                )
            }
        }
    }
}