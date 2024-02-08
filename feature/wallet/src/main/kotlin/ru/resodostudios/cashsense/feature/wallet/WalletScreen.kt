package ru.resodostudios.cashsense.feature.wallet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.formattedDate
import ru.resodostudios.cashsense.feature.transaction.R
import ru.resodostudios.cashsense.feature.transaction.TransactionViewModel
import ru.resodostudios.cashsense.feature.transaction.transactions
import ru.resodostudios.cashsense.feature.transaction.R as transactionR

@Composable
internal fun WalletRoute(
    onBackClick: () -> Unit,
    onTransactionCreate: (String) -> Unit,
    onTransactionEdit: (String, String) -> Unit,
    walletViewModel: WalletViewModel = hiltViewModel(),
    transactionViewModel: TransactionViewModel = hiltViewModel()
) {
    val walletState by walletViewModel.walletUiState.collectAsStateWithLifecycle()

    WalletScreen(
        walletState = walletState,
        onBackClick = onBackClick,
        onTransactionCreate = onTransactionCreate,
        onTransactionEdit = onTransactionEdit,
        onTransactionDelete = transactionViewModel::deleteTransaction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WalletScreen(
    walletState: WalletUiState,
    onBackClick: () -> Unit,
    onTransactionCreate: (String) -> Unit,
    onTransactionEdit: (String, String) -> Unit,
    onTransactionDelete: (Transaction) -> Unit
) {
    when (walletState) {
        WalletUiState.Loading -> LoadingState()
        is WalletUiState.Success -> {
            val wallet = walletState.walletWithTransactionsAndCategories.wallet
            val transactions = walletState.walletWithTransactionsAndCategories.transactionsWithCategories.map { it.transaction }

            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    LargeTopAppBar(
                        title = {
                            Text(
                                text = wallet.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(CsIcons.ArrowBack),
                                    contentDescription = null
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { onTransactionCreate(wallet.id) }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(CsIcons.Add),
                                    contentDescription = stringResource(R.string.feature_transaction_add_transaction_icon_description)
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior
                    )
                },
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                content = { paddingValues ->
                    if (transactions.isNotEmpty()) {
                        val sortedTransactionsAndCategories =
                            walletState.walletWithTransactionsAndCategories.transactionsWithCategories
                                .sortedByDescending { it.transaction.date }
                                .groupBy { formattedDate(it.transaction.date) }
                                .toSortedMap(compareByDescending { it })

                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(300.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            transactions(
                                transactionsWithCategories = sortedTransactionsAndCategories,
                                currency = wallet.currency,
                                onEdit = { onTransactionEdit(it, wallet.id) },
                                onDelete = onTransactionDelete
                            )
                        }
                    } else {
                        EmptyState(
                            messageRes = transactionR.string.feature_transaction_transactions_empty,
                            animationRes = transactionR.raw.anim_transactions_empty
                        )
                    }
                }
            )
        }
    }
}