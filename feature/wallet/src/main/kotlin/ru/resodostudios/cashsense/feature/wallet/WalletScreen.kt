package ru.resodostudios.cashsense.feature.wallet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.Clock
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.feature.transaction.EditTransactionDialog
import ru.resodostudios.cashsense.feature.transaction.TransactionViewModel
import ru.resodostudios.cashsense.feature.transaction.transactions
import java.util.UUID

@Composable
internal fun WalletRoute(
    walletViewModel: WalletViewModel = hiltViewModel(),
    transactionViewModel: TransactionViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val walletState by walletViewModel.walletUiState.collectAsStateWithLifecycle()

    WalletScreen(
        walletState = walletState,
        onBackClick = onBackClick,
        onDelete = {
            transactionViewModel.deleteTransactionCategoryCrossRef(it.transactionId)
            transactionViewModel.deleteTransaction(it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WalletScreen(
    walletState: WalletUiState,
    onBackClick: () -> Unit,
    onDelete: (Transaction) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var showAddTransactionDialog by rememberSaveable { mutableStateOf(false) }
    var showEditTransactionDialog by rememberSaveable { mutableStateOf(false) }

    var transactionWithCategoryState by rememberSaveable {
        mutableStateOf(
            TransactionWithCategory(
                transaction = Transaction(
                    transactionId = UUID.randomUUID(),
                    walletOwnerId = 0L,
                    amount = 0.0,
                    description = null,
                    date = Clock.System.now()
                ),
                category = Category()
            )
        )
    }

    when (walletState) {
        WalletUiState.Loading -> LoadingState()
        is WalletUiState.Success -> {
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = walletState.walletWithTransactionsAndCategories.wallet.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(imageVector = CsIcons.ArrowBack, contentDescription = null)
                            }
                        }
                    )
                },
                contentWindowInsets = WindowInsets.waterfall,
                content = { innerPadding ->
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(300.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = innerPadding
                    ) {
                        transactions(
                            transactionsWithCategories = walletState.walletWithTransactionsAndCategories.transactionsWithCategories,
                            currency = walletState.walletWithTransactionsAndCategories.wallet.currency,
                            onEdit = {
                                transactionWithCategoryState = it
                                showEditTransactionDialog = true
                            },
                            onDelete = onDelete
                        )
                    }
                    if (showEditTransactionDialog) {
                        EditTransactionDialog(
                            transactionWithCategory = transactionWithCategoryState,
                            onDismiss = { showEditTransactionDialog = false }
                        )
                    }
                }
            )
        }
    }
}