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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.model.data.Currency
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.model.data.WalletWithTransactionsAndCategories
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.feature.transaction.AddTransactionDialog
import ru.resodostudios.cashsense.feature.transaction.TransactionViewModel
import ru.resodostudios.cashsense.feature.wallet.EditWalletDialog
import ru.resodostudios.cashsense.feature.wallet.WalletCard
import ru.resodostudios.cashsense.feature.wallet.R as walletR

@Composable
internal fun HomeRoute(
    onWalletClick: (Long) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
    transactionViewModel: TransactionViewModel = hiltViewModel(),
) {
    val walletsState by homeViewModel.walletsUiState.collectAsStateWithLifecycle()
    HomeScreen(
        walletsState = walletsState,
        onWalletClick = onWalletClick,
        onDelete = { wallet, transactions ->
            homeViewModel.deleteWalletWithTransactions(wallet, transactions)
            transactions.forEach { transaction ->
                transactionViewModel.deleteTransactionCategoryCrossRef(transaction.transactionId)
            }
        }
    )
}

@Composable
internal fun HomeScreen(
    walletsState: WalletsUiState,
    onWalletClick: (Long) -> Unit,
    onDelete: (Wallet, List<Transaction>) -> Unit
) {
    var showAddTransactionDialog by rememberSaveable { mutableStateOf(false) }
    var showEditWalletDialog by rememberSaveable { mutableStateOf(false) }

    var walletId by rememberSaveable { mutableLongStateOf(0L) }
    var walletState by rememberSaveable {
        mutableStateOf(
            Wallet(
                title = "",
                startBalance = 0.0,
                currency = Currency.USD
            )
        )
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
                    onTransactionCreate = {
                        walletId = it
                        showAddTransactionDialog = true
                    },
                    onEdit = {
                        walletState = it
                        showEditWalletDialog = true
                    },
                    onDelete = onDelete
                )
            }
            if (showEditWalletDialog) {
                EditWalletDialog(
                    onDismiss = { showEditWalletDialog = false },
                    wallet = walletState
                )
            }
            if (showAddTransactionDialog) {
                AddTransactionDialog(
                    onDismiss = { showAddTransactionDialog = false },
                    walletId = walletId
                )
            }
        } else {
            EmptyState(
                messageRes = walletR.string.wallets_empty,
                animationRes = walletR.raw.anim_wallet_empty
            )
        }
    }
}

private fun LazyStaggeredGridScope.walletsWithTransactionsAndCategories(
    walletsWithTransactionsAndCategories: List<WalletWithTransactionsAndCategories>,
    onWalletClick: (Long) -> Unit,
    onTransactionCreate: (Long) -> Unit,
    onEdit: (Wallet) -> Unit,
    onDelete: (Wallet, List<Transaction>) -> Unit
) {
    items(walletsWithTransactionsAndCategories) { walletWithTransactionsAndCategories ->
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