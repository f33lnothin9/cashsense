package ru.resodostudios.cashsense.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.model.data.WalletWithTransactionsAndCategories
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.feature.transactions.TransactionDialog
import ru.resodostudios.cashsense.feature.wallets.EditWalletDialog
import ru.resodostudios.cashsense.feature.wallets.R
import ru.resodostudios.cashsense.feature.wallets.WalletCard

@Composable
internal fun HomeRoute(
    onWalletClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val walletsState by viewModel.walletsUiState.collectAsStateWithLifecycle()
    HomeScreen(
        walletsState = walletsState,
        onWalletClick = onWalletClick,
        onDelete = viewModel::deleteWalletWithTransactions
    )
}

@Composable
internal fun HomeScreen(
    walletsState: WalletsUiState,
    onWalletClick: (Long) -> Unit,
    onDelete: (Wallet, List<Transaction>) -> Unit
) {
    var showNewTransactionDialog by rememberSaveable { mutableStateOf(false) }
    var showEditWalletDialog by rememberSaveable { mutableStateOf(false) }

    var walletId by rememberSaveable { mutableLongStateOf(0L) }
    var wallet by remember { mutableStateOf(Wallet()) }

    Box {
        when (walletsState) {
            WalletsUiState.Loading -> LoadingState()
            is WalletsUiState.Success -> if (walletsState.walletsWithTransactionsAndCategories.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(300.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    walletsWithTransactionsAndCategories(
                        walletsWithTransactionsAndCategories = walletsState.walletsWithTransactionsAndCategories,
                        onWalletClick = onWalletClick,
                        onTransactionCreate = {
                            walletId = it
                            showNewTransactionDialog = true
                        },
                        onEdit = {
                            wallet = it
                            showEditWalletDialog = true
                        },
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
        if (showEditWalletDialog) {
            EditWalletDialog(
                onDismiss = { showEditWalletDialog = false },
                wallet = wallet
            )
        }
        if (showNewTransactionDialog) {
            TransactionDialog(
                onDismiss = { showNewTransactionDialog = false },
                walletId = walletId
            )
        }
    }
}

private fun LazyGridScope.walletsWithTransactionsAndCategories(
    walletsWithTransactionsAndCategories: List<WalletWithTransactionsAndCategories>,
    onWalletClick: (Long) -> Unit,
    onTransactionCreate: (Long) -> Unit,
    onEdit: (Wallet) -> Unit,
    onDelete: (Wallet, List<Transaction>) -> Unit
) {
    items(walletsWithTransactionsAndCategories) { walletWithTransactionsAndCategories ->
        WalletCard(
            wallet = walletWithTransactionsAndCategories.wallet,
            transactionsWithCategories = walletWithTransactionsAndCategories.transactionsWithCategories,
            onWalletClick = onWalletClick,
            onTransactionCreate = onTransactionCreate,
            onEdit = onEdit,
            onDelete = onDelete
        )
    }
}