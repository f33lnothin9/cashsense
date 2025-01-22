package ru.resodostudios.cashsense.feature.wallet.detail

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.Month
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.filled.Star
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Add
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ArrowBack
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ChevronLeft
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ChevronRight
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Close
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Delete
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Star
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.DateType
import ru.resodostudios.cashsense.core.model.data.DateType.ALL
import ru.resodostudios.cashsense.core.model.data.DateType.MONTH
import ru.resodostudios.cashsense.core.model.data.DateType.WEEK
import ru.resodostudios.cashsense.core.model.data.DateType.YEAR
import ru.resodostudios.cashsense.core.model.data.FinanceType.EXPENSES
import ru.resodostudios.cashsense.core.model.data.FinanceType.INCOME
import ru.resodostudios.cashsense.core.model.data.FinanceType.NOT_SET
import ru.resodostudios.cashsense.core.model.data.TransactionFilter
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.model.data.UserWallet
import ru.resodostudios.cashsense.core.ui.TransactionCategoryPreviewParameterProvider
import ru.resodostudios.cashsense.core.ui.component.AnimatedAmount
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.component.TransactionBottomSheet
import ru.resodostudios.cashsense.core.ui.component.WalletDropdownMenu
import ru.resodostudios.cashsense.core.ui.transactions
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.ui.util.getCurrentYear
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.AddToSelectedCategories
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.DecrementSelectedDate
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.IncrementSelectedDate
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.RemoveFromSelectedCategories
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.UpdateDateType
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.UpdateFinanceType
import ru.resodostudios.cashsense.feature.wallet.detail.component.CategorySelectionRow
import ru.resodostudios.cashsense.feature.wallet.detail.component.FinanceGraph
import java.math.BigDecimal
import java.math.BigDecimal.ONE
import java.math.BigDecimal.ZERO
import java.math.MathContext
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Currency
import java.util.Locale
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun WalletScreen(
    onBackClick: () -> Unit,
    onTransfer: (String) -> Unit,
    onEditWallet: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    showNavigationIcon: Boolean,
    navigateToTransactionDialog: (walletId: String, transactionId: String?, repeated: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WalletViewModel = hiltViewModel(),
) {
    val walletState by viewModel.walletUiState.collectAsStateWithLifecycle()

    WalletScreen(
        walletState = walletState,
        showNavigationIcon = showNavigationIcon,
        onPrimaryClick = viewModel::setPrimaryWalletId,
        onTransfer = onTransfer,
        onEditWallet = onEditWallet,
        onDeleteWallet = onDeleteClick,
        onBackClick = onBackClick,
        navigateToTransactionDialog = navigateToTransactionDialog,
        onWalletEvent = viewModel::onWalletEvent,
        modifier = modifier,
        updateTransactionId = viewModel::updateTransactionId,
        onUpdateTransactionIgnoring = viewModel::updateTransactionIgnoring,
        onDeleteTransaction = viewModel::deleteTransaction,
    )
}

@Composable
private fun WalletScreen(
    walletState: WalletUiState,
    showNavigationIcon: Boolean,
    onPrimaryClick: (walletId: String, isPrimary: Boolean) -> Unit,
    onTransfer: (String) -> Unit,
    onEditWallet: (String) -> Unit,
    onDeleteWallet: (String) -> Unit,
    onBackClick: () -> Unit,
    navigateToTransactionDialog: (walletId: String, transactionId: String?, repeated: Boolean) -> Unit,
    onWalletEvent: (WalletEvent) -> Unit,
    modifier: Modifier = Modifier,
    updateTransactionId: (String) -> Unit = {},
    onUpdateTransactionIgnoring: (Boolean) -> Unit = {},
    onDeleteTransaction: () -> Unit = {},
) {
    when (walletState) {
        WalletUiState.Loading -> LoadingState(modifier.fillMaxSize())
        is WalletUiState.Success -> {
            var showTransactionBottomSheet by rememberSaveable { mutableStateOf(false) }
            var showTransactionDeletionDialog by rememberSaveable { mutableStateOf(false) }

            if (showTransactionBottomSheet && walletState.selectedTransactionCategory != null) {
                TransactionBottomSheet(
                    transactionCategory = walletState.selectedTransactionCategory,
                    currency = walletState.userWallet.currency,
                    onDismiss = { showTransactionBottomSheet = false },
                    onIgnoreClick = onUpdateTransactionIgnoring,
                    onRepeatClick = { transactionId ->
                        navigateToTransactionDialog(walletState.userWallet.id, transactionId, true)
                    },
                    onEdit = { transactionId ->
                        navigateToTransactionDialog(walletState.userWallet.id, transactionId, false)
                    },
                    onDelete = { showTransactionDeletionDialog = true },
                )
            }
            if (showTransactionDeletionDialog) {
                CsAlertDialog(
                    titleRes = localesR.string.permanently_delete,
                    confirmButtonTextRes = localesR.string.delete,
                    dismissButtonTextRes = localesR.string.cancel,
                    icon = CsIcons.Outlined.Delete,
                    onConfirm = {
                        onDeleteTransaction()
                        showTransactionDeletionDialog = false
                    },
                    onDismiss = { showTransactionDeletionDialog = false },
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(bottom = 88.dp),
                modifier = modifier.fillMaxSize(),
            ) {
                item {
                    WalletTopBar(
                        userWallet = walletState.userWallet,
                        showNavigationIcon = showNavigationIcon,
                        onBackClick = onBackClick,
                        onNewTransactionClick = {
                            navigateToTransactionDialog(walletState.userWallet.id, null, false)
                        },
                        onPrimaryClick = onPrimaryClick,
                        onTransferClick = onTransfer,
                        onEditClick = onEditWallet,
                        onDeleteClick = onDeleteWallet,
                    )
                }
                item {
                    FinancePanel(
                        walletState = walletState,
                        onWalletEvent = onWalletEvent,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
                transactions(
                    transactionsCategories = walletState.transactionsCategories,
                    onTransactionClick = {
                        updateTransactionId(it)
                        showTransactionBottomSheet = true
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WalletTopBar(
    userWallet: UserWallet,
    showNavigationIcon: Boolean,
    onBackClick: () -> Unit,
    onNewTransactionClick: () -> Unit,
    onPrimaryClick: (walletId: String, isPrimary: Boolean) -> Unit,
    onTransferClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = userWallet.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                AnimatedAmount(
                    targetState = userWallet.currentBalance,
                    label = "wallet_balance",
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = it.formatAmount(userWallet.currency),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        navigationIcon = {
            if (showNavigationIcon) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = CsIcons.Outlined.ArrowBack,
                        contentDescription = null,
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onNewTransactionClick) {
                Icon(
                    imageVector = CsIcons.Outlined.Add,
                    contentDescription = stringResource(localesR.string.add_transaction_icon_description),
                )
            }
            PrimaryIconButton(userWallet, onPrimaryClick)
            WalletDropdownMenu(
                onTransferClick = { onTransferClick(userWallet.id) },
                onEditClick = { onEditClick(userWallet.id) },
                onDeleteClick = { onDeleteClick(userWallet.id) },
            )
        },
        windowInsets = WindowInsets(0, 0, 0, 0),
    )
}

@Composable
private fun PrimaryIconButton(
    userWallet: UserWallet,
    onPrimaryClick: (walletId: String, isPrimary: Boolean) -> Unit,
) {
    val (primaryIcon, @StringRes primaryIconContentDescriptionRes) = if (userWallet.isPrimary) {
        CsIcons.Filled.Star to localesR.string.primary_icon_description
    } else {
        CsIcons.Outlined.Star to localesR.string.non_primary_icon_description
    }
    IconButton(onClick = { onPrimaryClick(userWallet.id, !userWallet.isPrimary) }) {
        Icon(
            imageVector = primaryIcon,
            contentDescription = stringResource(primaryIconContentDescriptionRes),
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun FinancePanel(
    walletState: WalletUiState.Success,
    onWalletEvent: (WalletEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SharedTransitionLayout {
            AnimatedContent(
                targetState = walletState.transactionFilter.financeType,
                label = "finance_panel",
                transitionSpec = {
                    (fadeIn(animationSpec = tween(220, delayMillis = 90)) + scaleIn(
                        initialScale = 0.92f,
                        animationSpec = tween(220, delayMillis = 90),
                    )) togetherWith fadeOut(snap())
                },
            ) { financeType ->
                when (financeType) {
                    NOT_SET -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        ) {
                            val expensesProgress by animateFloatAsState(
                                targetValue = getFinanceProgress(
                                    walletState.expenses,
                                    walletState.transactionsCategories,
                                ),
                                label = "expenses_progress",
                                animationSpec = tween(durationMillis = 400),
                            )
                            val incomeProgress by animateFloatAsState(
                                targetValue = getFinanceProgress(
                                    walletState.income,
                                    walletState.transactionsCategories,
                                ),
                                label = "income_progress",
                                animationSpec = tween(durationMillis = 400),
                            )
                            FinanceCard(
                                title = walletState.expenses,
                                currency = walletState.userWallet.currency,
                                supportingTextId = localesR.string.expenses,
                                indicatorProgress = expensesProgress,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    onWalletEvent(UpdateFinanceType(EXPENSES))
                                    onWalletEvent(UpdateDateType(MONTH))
                                },
                                animatedVisibilityScope = this@AnimatedContent,
                            )
                            FinanceCard(
                                title = walletState.income,
                                currency = walletState.userWallet.currency,
                                supportingTextId = localesR.string.income_plural,
                                indicatorProgress = incomeProgress,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    onWalletEvent(UpdateFinanceType(INCOME))
                                    onWalletEvent(UpdateDateType(MONTH))
                                },
                                animatedVisibilityScope = this@AnimatedContent,
                            )
                        }
                    }

                    EXPENSES -> {
                        DetailedFinanceSection(
                            title = walletState.expenses,
                            graphData = walletState.graphData,
                            transactionFilter = walletState.transactionFilter,
                            currency = walletState.userWallet.currency,
                            supportingTextId = localesR.string.expenses,
                            onBackClick = {
                                onWalletEvent(UpdateFinanceType(NOT_SET))
                                onWalletEvent(UpdateDateType(ALL))
                            },
                            onWalletEvent = onWalletEvent,
                            modifier = Modifier.fillMaxWidth(),
                            animatedVisibilityScope = this@AnimatedContent,
                            availableCategories = walletState.availableCategories,
                        )
                    }

                    INCOME -> {
                        DetailedFinanceSection(
                            title = walletState.income,
                            graphData = walletState.graphData,
                            transactionFilter = walletState.transactionFilter,
                            currency = walletState.userWallet.currency,
                            supportingTextId = localesR.string.income_plural,
                            onBackClick = {
                                onWalletEvent(UpdateFinanceType(NOT_SET))
                                onWalletEvent(UpdateDateType(ALL))
                            },
                            onWalletEvent = onWalletEvent,
                            modifier = Modifier.fillMaxWidth(),
                            animatedVisibilityScope = this@AnimatedContent,
                            availableCategories = walletState.availableCategories,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.FinanceCard(
    title: BigDecimal,
    currency: Currency,
    @StringRes supportingTextId: Int,
    indicatorProgress: Float,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
) {
    OutlinedCard(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        onClick = onClick,
        enabled = enabled,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            AnimatedAmount(
                targetState = title,
                label = "finance_card_title",
                modifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState("$title/$supportingTextId"),
                    animatedVisibilityScope = animatedVisibilityScope,
                ),
            ) {
                Text(
                    text = it.formatAmount(currency),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = stringResource(supportingTextId),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(supportingTextId),
                    animatedVisibilityScope = animatedVisibilityScope,
                ),
            )
            LinearProgressIndicator(
                progress = { if (enabled) indicatorProgress else 0f },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.DetailedFinanceSection(
    title: BigDecimal,
    availableCategories: List<Category>,
    graphData: Map<Int, BigDecimal>,
    transactionFilter: TransactionFilter,
    currency: Currency,
    @StringRes supportingTextId: Int,
    onBackClick: () -> Unit,
    onWalletEvent: (WalletEvent) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp, start = 16.dp, end = 16.dp),
        ) {
            FilterDateTypeSelectorRow(
                transactionFilter = transactionFilter,
                onWalletEvent = onWalletEvent,
                modifier = Modifier
                    .defaultMinSize(minWidth = 400.dp)
                    .weight(1f, false),
            )
            FilledTonalIconButton(
                onClick = onBackClick,
                modifier = Modifier.padding(start = 12.dp),
            ) {
                Icon(
                    imageVector = CsIcons.Outlined.Close,
                    contentDescription = null,
                )
            }
        }
        AnimatedVisibility(transactionFilter.dateType != WEEK) {
            FilterBySelectedDateTypeRow(
                onWalletEvent = onWalletEvent,
                transactionFilter = transactionFilter,
                modifier = Modifier.padding(bottom = 6.dp, start = 16.dp, end = 16.dp),
            )
        }
        AnimatedAmount(
            targetState = title,
            label = "detailed_finance_card",
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState("$title/$supportingTextId"),
                    animatedVisibilityScope = animatedVisibilityScope,
                ),
        ) {
            Text(
                text = title.formatAmount(currency),
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = stringResource(supportingTextId),
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(supportingTextId),
                    animatedVisibilityScope = animatedVisibilityScope,
                ),
            style = MaterialTheme.typography.labelLarge,
        )
        AnimatedVisibility(graphData.isNotEmpty() && transactionFilter.financeType != NOT_SET) {
            FinanceGraph(
                transactionFilter = transactionFilter,
                graphData = graphData,
                currency = currency,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            )
        }
        AnimatedVisibility(transactionFilter.financeType != NOT_SET) {
            CategorySelectionRow(
                availableCategories = availableCategories,
                selectedCategories = transactionFilter.selectedCategories,
                addToSelectedCategories = { onWalletEvent(AddToSelectedCategories(it)) },
                removeFromSelectedCategories = { onWalletEvent(RemoveFromSelectedCategories(it)) },
                modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
            )
        }
    }
}

@Composable
private fun FilterDateTypeSelectorRow(
    transactionFilter: TransactionFilter,
    onWalletEvent: (WalletEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateTypes = listOf(
        stringResource(localesR.string.week),
        stringResource(localesR.string.month),
        stringResource(localesR.string.year),
    )
    SingleChoiceSegmentedButtonRow(modifier) {
        dateTypes.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = dateTypes.size,
                ),
                onClick = { onWalletEvent(UpdateDateType(DateType.entries[index])) },
                selected = transactionFilter.dateType == DateType.entries[index],
            ) {
                Text(
                    text = label,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun FilterBySelectedDateTypeRow(
    onWalletEvent: (WalletEvent) -> Unit,
    transactionFilter: TransactionFilter,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth(),
    ) {
        IconButton(
            onClick = { onWalletEvent(DecrementSelectedDate) },
        ) {
            Icon(
                imageVector = CsIcons.Outlined.ChevronLeft,
                contentDescription = null,
            )
        }

        val selectedDate = when (transactionFilter.dateType) {
            YEAR -> transactionFilter.selectedYearMonth.year.toString()
            MONTH -> {
                val monthName = Month(transactionFilter.selectedYearMonth.monthValue)
                    .getDisplayName(
                        TextStyle.FULL_STANDALONE,
                        Locale.getDefault()
                    )
                    .replaceFirstChar { it.uppercaseChar() }

                if (transactionFilter.selectedYearMonth.year != getCurrentYear()) {
                    "$monthName ${transactionFilter.selectedYearMonth.year}"
                } else {
                    monthName
                }
            }

            ALL, WEEK -> ""
        }

        Text(
            text = selectedDate,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        IconButton(
            onClick = { onWalletEvent(IncrementSelectedDate) },
        ) {
            Icon(
                imageVector = CsIcons.Outlined.ChevronRight,
                contentDescription = null,
            )
        }
    }
}

private fun getFinanceProgress(
    value: BigDecimal,
    transactions: List<TransactionWithCategory>,
): Float {
    if (transactions.isEmpty()) return 0f

    val totalAmount = transactions.sumOf { it.transaction.amount.abs() }
    if (totalAmount.compareTo(ZERO) == 0) return 0f

    return value.divide(totalAmount, MathContext.DECIMAL32).toFloat()
}

@Preview
@Composable
fun FinancePanelDefaultPreview(
    @PreviewParameter(TransactionCategoryPreviewParameterProvider::class)
    transactionsCategories: List<TransactionWithCategory>,
) {
    CsTheme {
        Surface {
            val categories = transactionsCategories.mapNotNullTo(HashSet()) { it.category }
            FinancePanel(
                walletState = WalletUiState.Success(
                    userWallet = UserWallet(
                        id = "1",
                        title = "Debit",
                        currency = getUsdCurrency(),
                        initialBalance = ZERO,
                        currentBalance = BigDecimal(100),
                        isPrimary = false,
                    ),
                    transactionFilter = TransactionFilter(
                        selectedCategories = categories,
                        financeType = NOT_SET,
                        dateType = YEAR,
                        selectedYearMonth = YearMonth.of(2025, 1),
                    ),
                    selectedTransactionCategory = null,
                    transactionsCategories = transactionsCategories,
                    availableCategories = categories.toList(),
                    income = BigDecimal(200),
                    expenses = BigDecimal(800),
                    graphData = emptyMap(),
                ),
                onWalletEvent = {},
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
            )
        }
    }
}

@Preview
@Composable
fun FinancePanelOpenedPreview(
    @PreviewParameter(TransactionCategoryPreviewParameterProvider::class)
    transactionsCategories: List<TransactionWithCategory>,
) {
    CsTheme {
        Surface {
            val categories = transactionsCategories.mapNotNull { it.category }
            FinancePanel(
                walletState = WalletUiState.Success(
                    userWallet = UserWallet(
                        id = "1",
                        title = "Debit",
                        currency = getUsdCurrency(),
                        initialBalance = ZERO,
                        currentBalance = BigDecimal(100),
                        isPrimary = false,
                    ),
                    transactionFilter = TransactionFilter(
                        selectedCategories = categories.take(3).toSet(),
                        financeType = EXPENSES,
                        dateType = YEAR,
                        selectedYearMonth = YearMonth.of(2025, 1),
                    ),
                    selectedTransactionCategory = null,
                    transactionsCategories = transactionsCategories,
                    availableCategories = categories.toList(),
                    income = ZERO,
                    expenses = ZERO,
                    graphData = mapOf(1 to ONE, 2 to ONE, 3 to ONE),
                ),
                onWalletEvent = {},
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
            )
        }
    }
}