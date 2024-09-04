package ru.resodostudios.cashsense.feature.wallet.detail

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberFadingEdges
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shape.Shape
import kotlinx.datetime.Month
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.ui.AnimatedAmount
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.StoredIcon
import ru.resodostudios.cashsense.core.ui.TransactionCategoryPreviewParameterProvider
import ru.resodostudios.cashsense.core.ui.formatAmount
import ru.resodostudios.cashsense.core.ui.formatDate
import ru.resodostudios.cashsense.core.ui.getZonedDateTime
import ru.resodostudios.cashsense.core.ui.isInCurrentMonthAndYear
import ru.resodostudios.cashsense.feature.transaction.TransactionBottomSheet
import ru.resodostudios.cashsense.feature.transaction.TransactionDialog
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateCurrency
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateTransactionId
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateWalletId
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogViewModel
import ru.resodostudios.cashsense.feature.transaction.TransactionItem
import ru.resodostudios.cashsense.feature.wallet.detail.DateType.ALL
import ru.resodostudios.cashsense.feature.wallet.detail.DateType.MONTH
import ru.resodostudios.cashsense.feature.wallet.detail.DateType.YEAR
import ru.resodostudios.cashsense.feature.wallet.detail.FinanceType.EXPENSES
import ru.resodostudios.cashsense.feature.wallet.detail.FinanceType.INCOME
import ru.resodostudios.cashsense.feature.wallet.detail.FinanceType.NONE
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.AddToSelectedCategories
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.ClearUndoState
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.DecrementSelectedDate
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.HideTransaction
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.IncrementSelectedDate
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.RemoveFromSelectedCategories
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.UndoTransactionRemoval
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.UpdateDateType
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.UpdateFinanceType
import ru.resodostudios.cashsense.feature.wallet.detail.WalletUiState.Loading
import ru.resodostudios.cashsense.feature.wallet.detail.WalletUiState.Success
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialog
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent.UpdateId
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogViewModel
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.math.MathContext
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import ru.resodostudios.cashsense.core.locales.R as localesR
import ru.resodostudios.cashsense.feature.transaction.R as transactionR

@Composable
internal fun WalletScreen(
    showDetailActions: Boolean,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    openTransactionDialog: Boolean,
    onTransactionDialogDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    walletViewModel: WalletViewModel = hiltViewModel(),
    walletDialogViewModel: WalletDialogViewModel = hiltViewModel(),
    transactionDialogViewModel: TransactionDialogViewModel = hiltViewModel(),
) {
    val walletState by walletViewModel.walletUiState.collectAsStateWithLifecycle()

    WalletScreen(
        walletState = walletState,
        showDetailActions = showDetailActions,
        onBackClick = onBackClick,
        onShowSnackbar = onShowSnackbar,
        openTransactionDialog = openTransactionDialog,
        onTransactionDialogDismiss = onTransactionDialogDismiss,
        onWalletEvent = walletViewModel::onWalletEvent,
        onWalletDialogEvent = walletDialogViewModel::onWalletDialogEvent,
        onTransactionEvent = transactionDialogViewModel::onTransactionEvent,
        modifier = modifier,
    )
}

@Composable
internal fun WalletScreen(
    walletState: WalletUiState,
    showDetailActions: Boolean,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    openTransactionDialog: Boolean,
    onTransactionDialogDismiss: () -> Unit,
    onWalletEvent: (WalletEvent) -> Unit,
    onWalletDialogEvent: (WalletDialogEvent) -> Unit,
    onTransactionEvent: (TransactionDialogEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler {
        onBackClick()
        onWalletEvent(ClearUndoState)
    }

    when (walletState) {
        Loading -> LoadingState(modifier.fillMaxSize())
        is Success -> {
            val transactionDeletedMessage = stringResource(localesR.string.transaction_deleted)
            val undoText = stringResource(localesR.string.undo)

            LaunchedEffect(walletState.shouldDisplayUndoTransaction) {
                if (walletState.shouldDisplayUndoTransaction) {
                    val snackBarResult = onShowSnackbar(transactionDeletedMessage, undoText)
                    if (snackBarResult) {
                        onWalletEvent(UndoTransactionRemoval)
                    } else {
                        onWalletEvent(ClearUndoState)
                    }
                }
            }
            LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
                onWalletEvent(ClearUndoState)
            }

            var showWalletDialog by rememberSaveable { mutableStateOf(false) }
            var showTransactionBottomSheet by rememberSaveable { mutableStateOf(false) }
            var showTransactionDialog by rememberSaveable { mutableStateOf(false) }

            LazyColumn(
                contentPadding = PaddingValues(bottom = 88.dp),
                modifier = modifier.fillMaxSize(),
            ) {
                item {
                    WalletTopBar(
                        title = walletState.wallet.title,
                        currentBalance = walletState.currentBalance,
                        currency = walletState.wallet.currency,
                        showDetailActions = showDetailActions,
                        onBackClick = onBackClick,
                        onWalletEvent = onWalletEvent,
                        onNewTransactionClick = {
                            onTransactionEvent(UpdateWalletId(walletState.wallet.id))
                            onTransactionEvent(UpdateTransactionId(""))
                            showTransactionDialog = true
                        },
                        onEditWalletClick = {
                            onWalletDialogEvent(UpdateId(walletState.wallet.id))
                            showWalletDialog = true
                        },
                    )
                }
                item {
                    FinancePanel(
                        walletState = walletState,
                        onWalletEvent = onWalletEvent,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                    )
                }
                if (walletState.transactionsCategories.isNotEmpty()) {
                    transactions(
                        transactionsCategories = walletState.transactionsCategories,
                        currency = walletState.wallet.currency,
                        onTransactionClick = {
                            onTransactionEvent(UpdateWalletId(walletState.wallet.id))
                            onTransactionEvent(UpdateTransactionId(it))
                            onTransactionEvent(UpdateCurrency(walletState.wallet.currency))
                            showTransactionBottomSheet = true
                        },
                    )
                } else {
                    item {
                        EmptyState(
                            messageRes = localesR.string.transactions_empty,
                            animationRes = transactionR.raw.anim_transactions_empty,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                        )
                    }
                }
            }
            if (showWalletDialog) {
                WalletDialog(onDismiss = { showWalletDialog = false })
            }

            if (showTransactionBottomSheet) {
                TransactionBottomSheet(
                    onDismiss = { showTransactionBottomSheet = false },
                    onEdit = { showTransactionDialog = true },
                    onDelete = { onWalletEvent(HideTransaction(it)) },
                )
            }
            if (showTransactionDialog) {
                TransactionDialog(
                    onDismiss = {
                        showTransactionDialog = false
                        onTransactionDialogDismiss()
                    }
                )
            }
            LaunchedEffect(openTransactionDialog) {
                if (openTransactionDialog) {
                    onTransactionEvent(UpdateWalletId(walletState.wallet.id))
                    onTransactionEvent(UpdateTransactionId(""))
                    showTransactionDialog = true
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WalletTopBar(
    title: String,
    currentBalance: BigDecimal,
    currency: String,
    showDetailActions: Boolean,
    onBackClick: () -> Unit,
    onWalletEvent: (WalletEvent) -> Unit,
    onNewTransactionClick: () -> Unit,
    onEditWalletClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                AnimatedAmount(
                    targetState = currentBalance,
                    label = "wallet_balance",
                    content = {
                        Text(
                            text = it.formatAmount(currency),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(-1f),
                )
            }
        },
        navigationIcon = {
            if (showDetailActions) {
                IconButton(
                    onClick = {
                        onBackClick()
                        onWalletEvent(ClearUndoState)
                    },
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(CsIcons.ArrowBack),
                        contentDescription = null,
                    )
                }
            }
        },
        actions = {
            IconButton(onNewTransactionClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(CsIcons.Add),
                    contentDescription = stringResource(localesR.string.add_transaction_icon_description),
                )
            }
            if (showDetailActions) {
                IconButton(onEditWalletClick) {
                    Icon(
                        imageVector = ImageVector.vectorResource(CsIcons.Edit),
                        contentDescription = null,
                    )
                }
            }
        },
        windowInsets = WindowInsets(0, 0, 0, 0),
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun FinancePanel(
    walletState: Success,
    onWalletEvent: (WalletEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val validTransactions = walletState.transactionsCategories
        .filterNot { it.transaction.ignored }
    val currentMonthTransactions = when (walletState.walletFilter.dateType) {
        ALL -> validTransactions.filter {
            it.transaction.timestamp.getZonedDateTime().isInCurrentMonthAndYear()
        }

        else -> validTransactions
    }
    val expenses = currentMonthTransactions
        .filter { it.transaction.amount < ZERO }
        .sumOf { it.transaction.amount.abs() }
    val income = currentMonthTransactions
        .filter { it.transaction.amount > ZERO }
        .sumOf { it.transaction.amount }

    Column(
        modifier = modifier.animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SharedTransitionLayout {
            AnimatedContent(
                targetState = walletState.walletFilter.financeType,
                label = "finance_panel",
            ) { financeType ->
                val groupedByMonth = currentMonthTransactions
                    .filter { it.transaction.timestamp.getZonedDateTime().year == walletState.walletFilter.selectedDate }
                    .groupBy { it.transaction.timestamp.getZonedDateTime().monthValue }
                when (financeType) {
                    NONE -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            val expensesProgress by animateFloatAsState(
                                targetValue = getFinanceProgress(expenses, currentMonthTransactions),
                                label = "expenses_progress",
                                animationSpec = tween(durationMillis = 400),
                            )
                            val incomeProgress by animateFloatAsState(
                                targetValue = getFinanceProgress(income, currentMonthTransactions),
                                label = "income_progress",
                                animationSpec = tween(durationMillis = 400),
                            )
                            FinanceCard(
                                title = expenses,
                                currency = walletState.wallet.currency,
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
                                title = income,
                                currency = walletState.wallet.currency,
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
                        val graphValues = groupedByMonth
                            .map { monthTransactions ->
                                monthTransactions.key to monthTransactions.value
                                    .map { transactionCategory -> transactionCategory.transaction.amount }
                                    .sumOf { it.abs() }
                            }
                            .associate { it.first to it.second }
                        DetailedFinanceSection(
                            title = expenses,
                            graphValues = graphValues,
                            walletFilter = walletState.walletFilter,
                            currency = walletState.wallet.currency,
                            supportingTextId = localesR.string.expenses,
                            onBackClick = {
                                onWalletEvent(UpdateFinanceType(NONE))
                                onWalletEvent(UpdateDateType(ALL))
                            },
                            onWalletEvent = onWalletEvent,
                            modifier = Modifier.fillMaxWidth(),
                            animatedVisibilityScope = this@AnimatedContent,
                        )
                    }

                    INCOME -> {
                        val graphValues = groupedByMonth
                            .map { monthTransactions ->
                                monthTransactions.key to monthTransactions.value
                                    .map { transactionCategory -> transactionCategory.transaction.amount }
                                    .sumOf { it }
                            }
                            .associate { it.first to it.second }
                        DetailedFinanceSection(
                            title = income,
                            graphValues = graphValues,
                            walletFilter = walletState.walletFilter,
                            currency = walletState.wallet.currency,
                            supportingTextId = localesR.string.income_plural,
                            onBackClick = {
                                onWalletEvent(UpdateFinanceType(NONE))
                                onWalletEvent(UpdateDateType(ALL))
                            },
                            onWalletEvent = onWalletEvent,
                            modifier = Modifier.fillMaxWidth(),
                            animatedVisibilityScope = this@AnimatedContent,
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
    currency: String,
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
                content = {
                    Text(
                        text = it.formatAmount(currency),
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                modifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState("$title/$supportingTextId"),
                    animatedVisibilityScope = animatedVisibilityScope,
                ),
            )
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
    graphValues: Map<Int, BigDecimal>,
    walletFilter: WalletFilter,
    currency: String,
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
                .padding(bottom = 6.dp)
                .fillMaxWidth(),
        ) {
            FilterDateTypeSelectorRow(
                dateType = walletFilter.dateType,
                onWalletEvent = onWalletEvent,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .weight(1f),
            )
            FilledTonalIconButton(onBackClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(CsIcons.Close),
                    contentDescription = null,
                )
            }
        }
        AnimatedVisibility(walletFilter.dateType == YEAR) {
            FilterBySelectedDateTypeRow(
                onWalletEvent = onWalletEvent,
                availableDates = walletFilter.availableDates,
                selectedDate = walletFilter.selectedDate,
                modifier = Modifier.padding(bottom = 6.dp),
            )
        }
        AnimatedAmount(
            targetState = title,
            label = "detailed_finance_card",
            content = {
                Text(
                    text = title.formatAmount(currency),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            modifier = Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState("$title/$supportingTextId"),
                animatedVisibilityScope = animatedVisibilityScope,
            ),
        )
        Text(
            text = stringResource(supportingTextId),
            modifier = Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(supportingTextId),
                animatedVisibilityScope = animatedVisibilityScope,
            ),
            style = MaterialTheme.typography.labelLarge,
        )
        if (walletFilter.dateType == YEAR) {
            FinanceGraph(graphValues)
        }
        if (walletFilter.dateType != ALL) {
            CategoryFilterRow(
                availableCategories = walletFilter.availableCategories,
                selectedCategories = walletFilter.selectedCategories,
                addToSelectedCategories = { onWalletEvent(AddToSelectedCategories(it)) },
                removeFromSelectedCategories = { onWalletEvent(RemoveFromSelectedCategories(it)) },
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun FinanceGraph(
    graphValues: Map<Int, BigDecimal>,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberVicoScrollState()
    val zoomState = rememberVicoZoomState(initialZoom = Zoom.max(Zoom.Content, Zoom.Content))
    val modelProducer = remember { CartesianChartModelProducer() }
    val xDateFormatter = CartesianValueFormatter { x, _, _ ->
        Month(x.toInt())
            .getDisplayName(TextStyle.SHORT, Locale.getDefault())
            .first()
            .uppercase()
    }
    val marker = rememberDefaultCartesianMarker(
        label = TextComponent(
            padding = Dimensions(
                startDp = 8f,
                endDp = 8f,
                topDp = 4f,
                bottomDp = 8f,
            ),
            background = ShapeComponent(
                color = MaterialTheme.colorScheme.primaryContainer.toArgb(),
                shape = Shape.Pill,
                margins = Dimensions(
                    startDp = 0f,
                    endDp = 0f,
                    topDp = 0f,
                    bottomDp = 4f,
                )
            ),
        ),
        labelPosition = DefaultCartesianMarker.LabelPosition.AbovePoint,
    )

    LaunchedEffect(graphValues) {
        modelProducer.runTransaction {
            if (graphValues.isNotEmpty()) {
                columnSeries { series(graphValues.keys, graphValues.values) }
            }
        }
    }
    ProvideVicoTheme(rememberM3VicoTheme()) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(),
                bottomAxis = rememberBottomAxis(
                    valueFormatter = xDateFormatter,
                    guideline = null,
                ),
                marker = marker,
                fadingEdges = rememberFadingEdges(),
            ),
            modelProducer = modelProducer,
            scrollState = scrollState,
            zoomState = zoomState,
            modifier = modifier,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryFilterRow(
    availableCategories: List<Category>,
    selectedCategories: List<Category>,
    addToSelectedCategories: (Category) -> Unit,
    removeFromSelectedCategories: (Category) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selected by rememberSaveable { mutableStateOf(false) }

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        availableCategories.forEach { category ->
            selected = selectedCategories.contains(category)
            CategoryChip(
                selected = selected,
                category = category,
                onClick = {
                    if (selectedCategories.contains(category)) {
                        removeFromSelectedCategories(category)
                    } else {
                        addToSelectedCategories(category)
                    }
                },
            )
        }
    }
}

@Composable
private fun CategoryChip(
    selected: Boolean,
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val categoryIconRes = if (selected) {
        CsIcons.Check
    } else {
        StoredIcon.asRes(category.iconId ?: StoredIcon.CATEGORY.storedId)
    }
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(category.title.toString()) },
        leadingIcon = {
            Icon(
                imageVector = ImageVector.vectorResource(categoryIconRes),
                contentDescription = null,
                modifier = modifier.size(FilterChipDefaults.IconSize),
            )
        }
    )
}

@Composable
private fun FilterDateTypeSelectorRow(
    dateType: DateType,
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
                selected = dateType == DateType.entries[index],
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
    availableDates: List<Int>,
    selectedDate: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth(),
    ) {
        val isPreviousActive =
            if (availableDates.isNotEmpty()) selectedDate != availableDates.first() else false
        IconButton(
            onClick = { onWalletEvent(DecrementSelectedDate) },
            enabled = isPreviousActive,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(CsIcons.ChevronLeft),
                contentDescription = null,
            )
        }
        Text(selectedDate.toString())
        val isNextActive =
            if (availableDates.isNotEmpty()) selectedDate != availableDates.last() else false
        IconButton(
            onClick = { onWalletEvent(IncrementSelectedDate) },
            enabled = isNextActive,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(CsIcons.ChevronRight),
                contentDescription = null,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.transactions(
    transactionsCategories: List<TransactionWithCategory>,
    currency: String,
    onTransactionClick: (String) -> Unit,
) {
    val groupedTransactionsCategories = transactionsCategories
        .groupBy {
            it.transaction.timestamp
                .toJavaInstant()
                .truncatedTo(ChronoUnit.DAYS)
        }
        .toSortedMap(compareByDescending { it })
        .map { it.key to it.value }

    groupedTransactionsCategories.forEach { group ->
        stickyHeader {
            CsTag(
                text = group.first
                    .toKotlinInstant()
                    .formatDate(),
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp),
            )
        }
        item { Spacer(Modifier.height(16.dp)) }
        items(
            items = group.second,
            key = { it.transaction.id },
            contentType = { "transactionCategory" },
        ) { transactionCategory ->
            val transaction = transactionCategory.transaction
            val category = transactionCategory.category

            TransactionItem(
                amount = transaction.amount.formatAmount(
                    currencyCode = currency,
                    withPlus = true,
                ),
                icon = category?.iconId ?: StoredIcon.TRANSACTION.storedId,
                categoryTitle = category?.title ?: stringResource(localesR.string.none),
                transactionStatus = transaction.status,
                ignored = transaction.ignored,
                onClick = { onTransactionClick(transaction.id) },
                modifier = Modifier.animateItem(),
            )
        }
    }
}

private fun getFinanceProgress(value: BigDecimal, transactions: List<TransactionWithCategory>) =
    if (transactions.isNotEmpty()) value
        .divide(
            transactions.sumOf { it.transaction.amount.abs() },
            MathContext.DECIMAL32,
        )
        .toFloat() else 0f

@Preview
@Composable
fun FinancePanelDefaultPreview(
    @PreviewParameter(TransactionCategoryPreviewParameterProvider::class)
    transactionsCategories: List<TransactionWithCategory>,
) {
    CsTheme {
        Surface {
            val categories = transactionsCategories.mapNotNull { it.category }
            FinancePanel(
                walletState = Success(
                    wallet = Wallet(
                        id = "1",
                        title = "Debit",
                        currency = "USD",
                        initialBalance = ZERO,
                    ),
                    currentBalance = BigDecimal.valueOf(100),
                    walletFilter = WalletFilter(
                        availableCategories = categories,
                        selectedCategories = categories.take(3),
                        financeType = NONE,
                        dateType = YEAR,
                        availableDates = emptyList(),
                        selectedDate = 0,
                    ),
                    transactionsCategories = transactionsCategories,
                    shouldDisplayUndoTransaction = false,
                ),
                onWalletEvent = {},
                modifier = Modifier.padding(16.dp),
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
            val categories = transactionsCategories
                .mapNotNull { it.category }
                .toSet()
                .toList()

            FinancePanel(
                walletState = Success(
                    wallet = Wallet(
                        id = "1",
                        title = "Debit",
                        currency = "USD",
                        initialBalance = ZERO,
                    ),
                    currentBalance = BigDecimal.valueOf(100),
                    walletFilter = WalletFilter(
                        availableCategories = categories,
                        selectedCategories = categories.take(2),
                        financeType = EXPENSES,
                        dateType = YEAR,
                        availableDates = emptyList(),
                        selectedDate = 0,
                    ),
                    transactionsCategories = transactionsCategories,
                    shouldDisplayUndoTransaction = false,

                    ),
                onWalletEvent = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}