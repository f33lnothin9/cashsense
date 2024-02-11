package ru.resodostudios.cashsense.feature.wallet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.ui.EditAndDeleteDropdownMenu
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.formattedDate
import ru.resodostudios.cashsense.core.ui.getFormattedAmountAndCurrency
import ru.resodostudios.cashsense.feature.transaction.TransactionViewModel
import java.time.temporal.ChronoUnit
import ru.resodostudios.cashsense.core.ui.R as uiR
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
            val transactionsAndCategories = walletState.walletWithTransactionsAndCategories.transactionsWithCategories

            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    LargeTopAppBar(
                        title = {
                            Text(
                                text = wallet.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(CsIcons.ArrowBack),
                                    contentDescription = null,
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { onTransactionCreate(wallet.id) }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(CsIcons.Add),
                                    contentDescription = stringResource(transactionR.string.feature_transaction_add_transaction_icon_description),
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior,
                    )
                },
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                content = { paddingValues ->
                    if (transactionsAndCategories.isNotEmpty()) {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(300.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                        ) {
                            item(
                                span = { GridItemSpan(maxLineSpan) }
                            ) {
                                FinanceSection(
                                    transactionsWithCategories = transactionsAndCategories,
                                    currency = wallet.currency,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                )
                            }
                            transactions(
                                transactionsWithCategories = transactionsAndCategories,
                                currency = wallet.currency,
                                onEdit = { onTransactionEdit(it, wallet.id) },
                                onDelete = onTransactionDelete,
                            )
                        }
                    } else {
                        EmptyState(
                            messageRes = transactionR.string.feature_transaction_transactions_empty,
                            animationRes = transactionR.raw.anim_transactions_empty,
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun FinanceSection(
    transactionsWithCategories: List<TransactionWithCategory>,
    currency: String,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier,
    ) {
        Card(
            modifier = Modifier.weight(1f),
            enabled = true,
            onClick = {},
            shape = RoundedCornerShape(20.dp),
        ) {
            val walletExpenses = transactionsWithCategories
                .asSequence()
                .filter { it.transaction.amount < 0.toBigDecimal() }
                .sumOf { it.transaction.amount }.abs()

            Text(
                text = getFormattedAmountAndCurrency(walletExpenses, currency),
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 12.dp),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
            Text(
                text = stringResource(R.string.feature_wallet_expenses),
                modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        Card(
            modifier = Modifier.weight(1f),
            enabled = true,
            onClick = {},
            shape = RoundedCornerShape(20.dp),
        ) {
            val walletIncome = transactionsWithCategories
                .asSequence()
                .filter { it.transaction.amount > 0.toBigDecimal() }
                .sumOf { it.transaction.amount }

            Text(
                text = getFormattedAmountAndCurrency(walletIncome, currency),
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 12.dp),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
            Text(
                text = stringResource(R.string.feature_wallet_income),
                modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

fun LazyGridScope.transactions(
    transactionsWithCategories: List<TransactionWithCategory>,
    currency: String,
    onEdit: (String) -> Unit,
    onDelete: (Transaction) -> Unit,
) {
    val sortedTransactionsAndCategories =
        transactionsWithCategories
            .sortedByDescending { it.transaction.date }
            .groupBy { it.transaction.date.toJavaInstant().truncatedTo(ChronoUnit.DAYS) }
            .toSortedMap(compareByDescending { it })
    val groupedTransactionsAndCategories = sortedTransactionsAndCategories.map { Pair(it.key, it.value) }

    groupedTransactionsAndCategories.forEach { group ->
        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Text(
                text = formattedDate(group.first.toKotlinInstant()),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(16.dp),
            )
        }
        items(
            items = group.second,
            key = { it.transaction.id },
            contentType = { "transactionWithCategory" },
        ) { transactionWithCategory ->
            val category = transactionWithCategory.category

            ListItem(
                headlineContent = {
                    Text(
                        text = getFormattedAmountAndCurrency(
                            amount = transactionWithCategory.transaction.amount,
                            currencyName = currency
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                trailingContent = {
                    EditAndDeleteDropdownMenu(
                        onEdit = { onEdit(transactionWithCategory.transaction.id) },
                        onDelete = { onDelete(transactionWithCategory.transaction) },
                    )
                },
                supportingContent = {
                    Text(
                        text = category?.title ?: stringResource(uiR.string.none),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = ImageVector.vectorResource(category?.iconRes ?: CsIcons.Transaction),
                        contentDescription = null,
                    )
                }
            )
        }
    }
}