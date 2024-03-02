package ru.resodostudios.cashsense.feature.wallet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.StoredIcon
import ru.resodostudios.cashsense.core.ui.formatAmountWithCurrency
import ru.resodostudios.cashsense.core.ui.formattedDate
import ru.resodostudios.cashsense.feature.transaction.TransactionBottomSheet
import ru.resodostudios.cashsense.feature.transaction.TransactionDialog
import ru.resodostudios.cashsense.feature.transaction.TransactionEvent
import ru.resodostudios.cashsense.feature.transaction.TransactionViewModel
import java.math.BigDecimal
import java.math.MathContext
import java.time.temporal.ChronoUnit
import ru.resodostudios.cashsense.core.ui.R as uiR
import ru.resodostudios.cashsense.feature.transaction.R as transactionR

@Composable
internal fun WalletRoute(
    onBackClick: () -> Unit,
    walletViewModel: WalletViewModel = hiltViewModel(),
    transactionViewModel: TransactionViewModel = hiltViewModel(),
) {
    val walletState by walletViewModel.walletUiState.collectAsStateWithLifecycle()

    WalletScreen(
        walletState = walletState,
        onBackClick = onBackClick,
        onTransactionEvent = transactionViewModel::onTransactionEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WalletScreen(
    walletState: WalletUiState,
    onBackClick: () -> Unit,
    onTransactionEvent: (TransactionEvent) -> Unit,
) {
    var showTransactionBottomSheet by rememberSaveable {
        mutableStateOf(false)
    }
    var showTransactionDialog by rememberSaveable {
        mutableStateOf(false)
    }

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
                            Column(
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = wallet.title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = (wallet.initialBalance + transactionsAndCategories
                                        .map { it.transaction }
                                        .sumOf { it.amount })
                                        .formatAmountWithCurrency(wallet.currency),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
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
                            IconButton(
                                onClick = {
                                    onTransactionEvent(TransactionEvent.UpdateWalletId(wallet.id))
                                    showTransactionDialog = true
                                }
                            ) {
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
                                onTransactionClick = {
                                    onTransactionEvent(TransactionEvent.UpdateId(it))
                                    showTransactionBottomSheet = true
                                },
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
    if (showTransactionBottomSheet) {
        TransactionBottomSheet(
            onDismiss = { showTransactionBottomSheet = false },
            onEdit = { showTransactionDialog = true },
        )
    }
    if (showTransactionDialog) {
        TransactionDialog(
            onDismiss = { showTransactionDialog = false },
        )
    }
}

@Composable
private fun FinanceSection(
    transactionsWithCategories: List<TransactionWithCategory>,
    currency: String,
    modifier: Modifier = Modifier,
) {
    val walletTransactions = transactionsWithCategories.sumOf { it.transaction.amount.abs() }

    val walletExpenses = transactionsWithCategories
        .asSequence()
        .filter { it.transaction.amount < BigDecimal(0) }
        .sumOf { it.transaction.amount.abs() }
    val walletIncome = transactionsWithCategories
        .asSequence()
        .filter { it.transaction.amount > BigDecimal(0) }
        .sumOf { it.transaction.amount }

    val expensesProgress =
        (walletExpenses.divide(walletTransactions, MathContext.DECIMAL32)).toFloat()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier,
    ) {
        OutlinedCard(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
        ) {
            Text(
                text = walletExpenses.formatAmountWithCurrency(currency),
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
            Text(
                text = stringResource(R.string.feature_wallet_expenses),
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.labelMedium,
            )
            LinearProgressIndicator(
                progress = { expensesProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
        }
        OutlinedCard(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
        ) {
            Text(
                text = walletIncome.formatAmountWithCurrency(currency),
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
            Text(
                text = stringResource(R.string.feature_wallet_income),
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.labelMedium,
            )
            LinearProgressIndicator(
                progress = { 1.0f - expensesProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
        }
    }
}

private fun LazyGridScope.transactions(
    transactionsWithCategories: List<TransactionWithCategory>,
    currency: String,
    onTransactionClick: (String) -> Unit,
) {
    val sortedTransactionsAndCategories =
        transactionsWithCategories
            .sortedByDescending { it.transaction.date }
            .groupBy { it.transaction.date.toJavaInstant().truncatedTo(ChronoUnit.DAYS) }
            .toSortedMap(compareByDescending { it })
    val groupedTransactionsAndCategories =
        sortedTransactionsAndCategories.map { Pair(it.key, it.value) }

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
                        text = transactionWithCategory.transaction.amount.formatAmountWithCurrency(
                            currency = currency,
                            withPlus = true,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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
                        imageVector = ImageVector.vectorResource(
                            StoredIcon.asRes(
                                category?.iconId ?: StoredIcon.TRANSACTION.storedId
                            )
                        ),
                        contentDescription = null,
                    )
                },
                modifier = Modifier.clickable {
                    onTransactionClick(transactionWithCategory.transaction.id)
                }
            )
        }
    }
}