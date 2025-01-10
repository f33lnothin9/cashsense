package ru.resodostudios.cashsense.feature.wallet.detail

import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisTickComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberFadingEdges
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Month
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.model.data.UserWallet
import ru.resodostudios.cashsense.core.ui.AnimatedAmount
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.StoredIcon
import ru.resodostudios.cashsense.core.ui.TransactionCategoryPreviewParameterProvider
import ru.resodostudios.cashsense.core.ui.WalletDropdownMenu
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.ui.util.getCurrentYear
import ru.resodostudios.cashsense.core.ui.util.getDecimalFormat
import ru.resodostudios.cashsense.core.ui.util.getZonedDateTime
import ru.resodostudios.cashsense.core.ui.util.isInCurrentMonthAndYear
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import ru.resodostudios.cashsense.feature.wallet.detail.DateType.ALL
import ru.resodostudios.cashsense.feature.wallet.detail.DateType.MONTH
import ru.resodostudios.cashsense.feature.wallet.detail.DateType.WEEK
import ru.resodostudios.cashsense.feature.wallet.detail.DateType.YEAR
import ru.resodostudios.cashsense.feature.wallet.detail.FinanceType.EXPENSES
import ru.resodostudios.cashsense.feature.wallet.detail.FinanceType.INCOME
import ru.resodostudios.cashsense.feature.wallet.detail.FinanceType.NONE
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.AddToSelectedCategories
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.DecrementSelectedDate
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.IncrementSelectedDate
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.RemoveFromSelectedCategories
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.UpdateDateType
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.UpdateFinanceType
import ru.resodostudios.cashsense.feature.wallet.detail.WalletUiState.Loading
import ru.resodostudios.cashsense.feature.wallet.detail.WalletUiState.Success
import java.math.BigDecimal
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
        Loading -> LoadingState(modifier.fillMaxSize())
        is Success -> {
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
                    iconRes = CsIcons.Delete,
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
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                    )
                }
                transactions(
                    walletState = walletState,
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
                        imageVector = ImageVector.vectorResource(CsIcons.ArrowBack),
                        contentDescription = null,
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onNewTransactionClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(CsIcons.Add),
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
    val (@DrawableRes primaryIconRes, @StringRes primaryIconContentDescriptionRes) = if (userWallet.isPrimary) {
        CsIcons.StarFilled to localesR.string.primary_icon_description
    } else {
        CsIcons.Star to localesR.string.non_primary_icon_description
    }
    IconButton(onClick = { onPrimaryClick(userWallet.id, !userWallet.isPrimary) }) {
        Icon(
            imageVector = ImageVector.vectorResource(primaryIconRes),
            contentDescription = stringResource(primaryIconContentDescriptionRes),
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun FinancePanel(
    walletState: Success,
    onWalletEvent: (WalletEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filteredTransactions = walletState.transactionsCategories
        .filterNot { it.transaction.ignored }
        .filter {
            if (walletState.walletFilter.dateType == ALL) {
                it.transaction.timestamp
                    .getZonedDateTime()
                    .isInCurrentMonthAndYear()
            } else true
        }
    val expenses = filteredTransactions
        .filter { it.transaction.amount < ZERO }
        .sumOf { it.transaction.amount.abs() }
    val income = filteredTransactions
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
                val groupedTransactions = filteredTransactions
                    .groupBy {
                        val zonedDateTime = it.transaction.timestamp.getZonedDateTime()
                        when (walletState.walletFilter.dateType) {
                            YEAR -> zonedDateTime.monthValue
                            MONTH -> zonedDateTime.dayOfMonth
                            ALL, WEEK -> zonedDateTime.dayOfWeek.value
                        }
                    }
                when (financeType) {
                    NONE -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            val expensesProgress by animateFloatAsState(
                                targetValue = getFinanceProgress(expenses, filteredTransactions),
                                label = "expenses_progress",
                                animationSpec = tween(durationMillis = 400),
                            )
                            val incomeProgress by animateFloatAsState(
                                targetValue = getFinanceProgress(income, filteredTransactions),
                                label = "income_progress",
                                animationSpec = tween(durationMillis = 400),
                            )
                            FinanceCard(
                                title = expenses,
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
                                title = income,
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
                        val graphValues = groupedTransactions
                            .map { transactionsCategories ->
                                transactionsCategories.key to transactionsCategories.value
                                    .map { transactionCategory -> transactionCategory.transaction.amount }
                                    .sumOf { it.abs() }
                            }
                            .associate { it.first to it.second }
                        DetailedFinanceSection(
                            title = expenses,
                            graphValues = graphValues,
                            walletFilter = walletState.walletFilter,
                            currency = walletState.userWallet.currency,
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
                        val graphValues = groupedTransactions
                            .map { transactionsCategories ->
                                transactionsCategories.key to transactionsCategories.value
                                    .map { transactionCategory -> transactionCategory.transaction.amount }
                                    .sumOf { it }
                            }
                            .associate { it.first to it.second }
                        DetailedFinanceSection(
                            title = income,
                            graphValues = graphValues,
                            walletFilter = walletState.walletFilter,
                            currency = walletState.userWallet.currency,
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
    graphValues: Map<Int, BigDecimal>,
    walletFilter: WalletFilter,
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
                .padding(bottom = 6.dp)
                .fillMaxWidth(),
        ) {
            FilterDateTypeSelectorRow(
                walletFilter = walletFilter,
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
                    imageVector = ImageVector.vectorResource(CsIcons.Close),
                    contentDescription = null,
                )
            }
        }
        AnimatedVisibility(walletFilter.dateType != WEEK) {
            FilterBySelectedDateTypeRow(
                onWalletEvent = onWalletEvent,
                walletFilter = walletFilter,
                modifier = Modifier.padding(bottom = 6.dp),
            )
        }
        AnimatedAmount(
            targetState = title,
            label = "detailed_finance_card",
            modifier = Modifier.sharedBounds(
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
            modifier = Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(supportingTextId),
                animatedVisibilityScope = animatedVisibilityScope,
            ),
            style = MaterialTheme.typography.labelLarge,
        )
        if (graphValues.isNotEmpty() && walletFilter.dateType != ALL) {
            Box(contentAlignment = Alignment.Center) {
                if (graphValues.keys.size < 2) {
                    Text(
                        text = stringResource(localesR.string.not_enough_data),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                FinanceGraph(
                    walletFilter = walletFilter,
                    graphValues = graphValues,
                    currency = currency,
                )
            }
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
    walletFilter: WalletFilter,
    graphValues: Map<Int, BigDecimal>,
    currency: Currency,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberVicoScrollState()
    val zoomState = rememberVicoZoomState(initialZoom = Zoom.max(Zoom.Content, Zoom.Content))
    val modelProducer = remember { CartesianChartModelProducer() }
    val xDateFormatter = CartesianValueFormatter { _, x, _ ->
        when (walletFilter.dateType) {
            YEAR -> Month(x.toInt().coerceIn(1, 12)).getDisplayName(
                TextStyle.NARROW_STANDALONE,
                Locale.getDefault(),
            )

            MONTH -> x.toInt().toString()
            ALL, WEEK -> DayOfWeek(x.toInt().coerceIn(1, 7)).getDisplayName(
                TextStyle.NARROW_STANDALONE,
                Locale.getDefault(),
            )
        }
    }

    val marker = rememberDefaultCartesianMarker(
        label = TextComponent(
            textSizeSp = 14f,
            padding = Insets(
                startDp = 8f,
                endDp = 8f,
                topDp = 4f,
                bottomDp = 20f,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant.toArgb(),
            background = ShapeComponent(
                fill = Fill(MaterialTheme.colorScheme.surfaceVariant.toArgb()),
                shape = CorneredShape.Pill,
                margins = Insets(
                    startDp = 0f,
                    endDp = 0f,
                    topDp = 0f,
                    bottomDp = 16f,
                )
            ),
        ),
        labelPosition = DefaultCartesianMarker.LabelPosition.AbovePoint,
        valueFormatter = DefaultCartesianMarker.ValueFormatter.default(getDecimalFormat(currency)),
    )

    LaunchedEffect(graphValues) {
        modelProducer.runTransaction {
            if (graphValues.isNotEmpty() && graphValues.keys.size > 1) {
                lineSeries { series(graphValues.keys, graphValues.values) }
            }
        }
    }
    ProvideVicoTheme(rememberM3VicoTheme()) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    lineProvider = LineCartesianLayer.LineProvider.series(
                        vicoTheme.lineCartesianLayerColors.map { color ->
                            LineCartesianLayer.rememberLine(
                                pointConnector = LineCartesianLayer.PointConnector.cubic(),
                                areaFill = LineCartesianLayer.AreaFill.single(
                                    fill(
                                        ShaderProvider.verticalGradient(
                                            arrayOf(color.copy(alpha = 0.15f), Color.Transparent),
                                        )
                                    )
                                ),
                            )
                        }
                    ),
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = xDateFormatter,
                    guideline = null,
                    line = null,
                    tick = rememberAxisTickComponent(
                        margins = Insets(
                            startDp = 0f,
                            endDp = 0f,
                            topDp = 2f,
                            bottomDp = -2f,
                        ),
                    )
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
    val categoryIconRes = if (selected) CsIcons.Check else StoredIcon.asRes(category.iconId)
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
    walletFilter: WalletFilter,
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
                selected = walletFilter.dateType == DateType.entries[index],
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
    walletFilter: WalletFilter,
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
                imageVector = ImageVector.vectorResource(CsIcons.ChevronLeft),
                contentDescription = null,
            )
        }

        val selectedDate = when (walletFilter.dateType) {
            YEAR -> walletFilter.selectedYearMonth.year.toString()
            MONTH -> {
                val monthName = Month(walletFilter.selectedYearMonth.monthValue)
                    .getDisplayName(
                        TextStyle.FULL_STANDALONE,
                        Locale.getDefault()
                    )
                    .replaceFirstChar { it.uppercaseChar() }

                if (walletFilter.selectedYearMonth.year != getCurrentYear()) {
                    "$monthName ${walletFilter.selectedYearMonth.year}"
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
                imageVector = ImageVector.vectorResource(CsIcons.ChevronRight),
                contentDescription = null,
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
                    userWallet = UserWallet(
                        id = "1",
                        title = "Debit",
                        currency = getUsdCurrency(),
                        initialBalance = ZERO,
                        currentBalance = BigDecimal(100),
                        isPrimary = false,
                    ),
                    walletFilter = WalletFilter(
                        availableCategories = categories,
                        selectedCategories = categories.take(3),
                        financeType = NONE,
                        dateType = YEAR,
                        selectedYearMonth = YearMonth.of(2025, 1),
                    ),
                    selectedTransactionCategory = null,
                    transactionsCategories = transactionsCategories,
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
                    userWallet = UserWallet(
                        id = "1",
                        title = "Debit",
                        currency = getUsdCurrency(),
                        initialBalance = ZERO,
                        currentBalance = BigDecimal(100),
                        isPrimary = false,
                    ),
                    walletFilter = WalletFilter(
                        availableCategories = categories,
                        selectedCategories = categories.take(2),
                        financeType = EXPENSES,
                        dateType = YEAR,
                        selectedYearMonth = YearMonth.of(2025, 1),
                    ),
                    selectedTransactionCategory = null,
                    transactionsCategories = transactionsCategories,
                ),
                onWalletEvent = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}