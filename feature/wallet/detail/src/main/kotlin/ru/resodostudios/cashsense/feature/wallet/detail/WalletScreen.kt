package ru.resodostudios.cashsense.feature.wallet.detail

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
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
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toKotlinLocalDate
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
import ru.resodostudios.cashsense.core.ui.getCurrentZonedDateTime
import ru.resodostudios.cashsense.core.ui.getZonedDateTime
import ru.resodostudios.cashsense.feature.transaction.TransactionBottomSheet
import ru.resodostudios.cashsense.feature.transaction.TransactionDialog
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateCurrency
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateTransactionId
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateWalletId
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogViewModel
import ru.resodostudios.cashsense.feature.transaction.TransactionItem
import ru.resodostudios.cashsense.feature.wallet.detail.FinanceSectionType.EXPENSES
import ru.resodostudios.cashsense.feature.wallet.detail.FinanceSectionType.INCOME
import ru.resodostudios.cashsense.feature.wallet.detail.FinanceSectionType.NONE
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.AddToSelectedCategories
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.ClearUndoState
import ru.resodostudios.cashsense.feature.wallet.detail.WalletEvent.HideTransaction
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
import java.math.MathContext
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import ru.resodostudios.cashsense.core.ui.R as uiR
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
            val transactionDeletedMessage =
                stringResource(transactionR.string.feature_transaction_deleted)
            val undoText = stringResource(uiR.string.core_ui_undo)

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
                contentPadding = PaddingValues(
                    bottom = 88.dp,
                ),
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
                            messageRes = transactionR.string.feature_transaction_transactions_empty,
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
            IconButton(
                onClick = onNewTransactionClick,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(CsIcons.Add),
                    contentDescription = stringResource(transactionR.string.feature_transaction_add_transaction_icon_description),
                )
            }
            if (showDetailActions) {
                IconButton(
                    onClick = onEditWalletClick,
                ) {
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
    walletState: WalletUiState,
    onWalletEvent: (WalletEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (walletState) {
        Loading -> Unit
        is Success -> {
            val expenses = walletState.transactionsCategories
                .filter { it.transaction.amount < BigDecimal.ZERO && !it.transaction.ignored }
                .sumOf { it.transaction.amount.abs() }
            val income = walletState.transactionsCategories
                .filter { it.transaction.amount > BigDecimal.ZERO && !it.transaction.ignored }
                .sumOf { it.transaction.amount }

            val notIgnoredTransactions = walletState.transactionsCategories
                .filterNot { it.transaction.ignored }
            val expensesProgress by animateFloatAsState(
                targetValue = if (notIgnoredTransactions.isNotEmpty()) expenses
                    .divide(
                        notIgnoredTransactions.sumOf { it.transaction.amount.abs() },
                        MathContext.DECIMAL32,
                    )
                    .toFloat() else 0f,
                label = "expenses_progress",
                animationSpec = tween(durationMillis = 400),
            )

            Column(
                modifier = modifier.animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SharedTransitionLayout {
                    AnimatedContent(
                        targetState = walletState.financeSectionType,
                        label = "finance_panel",
                    ) { financeType ->
                        when (financeType) {
                            NONE -> {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                ) {
                                    FinanceCard(
                                        title = expenses,
                                        currency = walletState.wallet.currency,
                                        supportingTextId = R.string.feature_wallet_detail_expenses,
                                        indicatorProgress = expensesProgress,
                                        modifier = Modifier.weight(1f),
                                        onClick = { onWalletEvent(UpdateFinanceType(EXPENSES)) },
                                        enabled = expenses != BigDecimal.ZERO,
                                        animatedVisibilityScope = this@AnimatedContent,
                                    )
                                    FinanceCard(
                                        title = income,
                                        currency = walletState.wallet.currency,
                                        supportingTextId = R.string.feature_wallet_detail_income,
                                        indicatorProgress = 1.0f - expensesProgress,
                                        modifier = Modifier.weight(1f),
                                        onClick = { onWalletEvent(UpdateFinanceType(INCOME)) },
                                        enabled = income != BigDecimal.ZERO,
                                        animatedVisibilityScope = this@AnimatedContent,
                                    )
                                }
                            }

                            EXPENSES -> {
                                val data = notIgnoredTransactions
                                    .filter { it.transaction.timestamp.getZonedDateTime().year == getCurrentZonedDateTime().year }
                                    .groupBy { it.transaction.timestamp.getZonedDateTime().monthValue }
                                    .map { monthTransactions ->
                                        monthTransactions.key to monthTransactions.value
                                            .map { transactionCategory -> transactionCategory.transaction.amount }
                                            .sumOf { it.abs() }
                                    }
                                    .associate { it.first to it.second }
                                DetailedFinanceCard(
                                    title = expenses,
                                    data = data,
                                    currency = walletState.wallet.currency,
                                    supportingTextId = R.string.feature_wallet_detail_expenses,
                                    availableCategories = walletState.availableCategories,
                                    selectedCategories = walletState.selectedCategories,
                                    onBackClick = { onWalletEvent(UpdateFinanceType(NONE)) },
                                    onWalletEvent = onWalletEvent,
                                    modifier = Modifier.fillMaxWidth(),
                                    animatedVisibilityScope = this@AnimatedContent,
                                )
                            }

                            INCOME -> {
                                val data = notIgnoredTransactions
                                    .filter { it.transaction.timestamp.getZonedDateTime().year == getCurrentZonedDateTime().year }
                                    .groupBy { it.transaction.timestamp.getZonedDateTime().monthValue }
                                    .map { monthTransactions ->
                                        monthTransactions.key to monthTransactions.value
                                            .map { transactionCategory -> transactionCategory.transaction.amount }
                                            .sumOf { it }
                                    }
                                    .associate { it.first to it.second }
                                DetailedFinanceCard(
                                    title = income,
                                    data = data,
                                    currency = walletState.wallet.currency,
                                    supportingTextId = R.string.feature_wallet_detail_income,
                                    availableCategories = walletState.availableCategories,
                                    selectedCategories = walletState.selectedCategories,
                                    onBackClick = { onWalletEvent(UpdateFinanceType(NONE)) },
                                    onWalletEvent = onWalletEvent,
                                    modifier = Modifier.fillMaxWidth(),
                                    animatedVisibilityScope = this@AnimatedContent,
                                )
                            }
                        }
                    }
                }
                val dateTypes = listOf(
                    stringResource(R.string.feature_wallet_detail_all),
                    stringResource(R.string.feature_wallet_detail_week),
                    stringResource(R.string.feature_wallet_detail_month),
                    stringResource(R.string.feature_wallet_detail_year),
                )
                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    dateTypes.forEachIndexed { index, label ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = dateTypes.size,
                            ),
                            onClick = { onWalletEvent(UpdateDateType(DateType.entries[index])) },
                            selected = walletState.dateType == DateType.entries[index],
                            colors = SegmentedButtonDefaults.colors(
                                inactiveContainerColor = Color.Transparent,
                            ),
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
private fun SharedTransitionScope.DetailedFinanceCard(
    title: BigDecimal,
    data: Map<Int, BigDecimal>,
    currency: String,
    @StringRes supportingTextId: Int,
    availableCategories: List<Category>,
    selectedCategories: List<Category>,
    onBackClick: () -> Unit,
    onWalletEvent: (WalletEvent) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(start = 16.dp, bottom = 4.dp)
                .fillMaxWidth(),
        ) {
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
                modifier = Modifier
                    .weight(1f, false)
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState("$title/$supportingTextId"),
                        animatedVisibilityScope = animatedVisibilityScope,
                    ),
            )
            IconButton(onBackClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(CsIcons.Close),
                    contentDescription = null,
                )
            }
        }
        Text(
            text = stringResource(supportingTextId),
            modifier = Modifier
                .padding(start = 16.dp)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(supportingTextId),
                    animatedVisibilityScope = animatedVisibilityScope,
                ),
            style = MaterialTheme.typography.labelLarge,
        )
        val scrollState = rememberVicoScrollState()
        val zoomState = rememberVicoZoomState()
        val modelProducer = remember { CartesianChartModelProducer() }
        val xDateFormatter = CartesianValueFormatter { x, _, _ ->
            val dateTimeFormatter = DateTimeFormatter.ofPattern("MMM")
            val year = getCurrentZonedDateTime().year
            val month = if (x.toInt().toString().length == 1) "0${x.toInt()}" else x.toInt()
            val instant = java.time.LocalDate.parse("$year-$month-01")
            val localDateTime = instant.toKotlinLocalDate()
            dateTimeFormatter
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.systemDefault())
                .format(localDateTime.toJavaLocalDate())
        }

        LaunchedEffect(Unit) {
            modelProducer.runTransaction {
                columnSeries {
                    series(
                        data.keys,
                        data.values,
                    )
                }
            }
        }
        ProvideVicoTheme(rememberM3VicoTheme()) {
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberColumnCartesianLayer(),
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(
                        valueFormatter = xDateFormatter,
                    ),
                ),
                modelProducer = modelProducer,
                scrollState = scrollState,
                zoomState = zoomState,
                modifier = Modifier.padding(16.dp),
            )
        }
        CategoryFilterRow(
            availableCategories = availableCategories,
            selectedCategories = selectedCategories,
            addToSelectedCategories = { onWalletEvent(AddToSelectedCategories(it)) },
            removeFromSelectedCategories = { onWalletEvent(RemoveFromSelectedCategories(it)) },
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp, end = 16.dp, top = 16.dp),
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
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(category.title.toString()) },
        leadingIcon = {
            Icon(
                imageVector = if (selected) {
                    ImageVector.vectorResource(CsIcons.Check)
                } else {
                    ImageVector.vectorResource(
                        StoredIcon.asRes(
                            category.iconId ?: StoredIcon.CATEGORY.storedId
                        )
                    )
                },
                contentDescription = null,
                modifier = modifier.size(FilterChipDefaults.IconSize),
            )
        }
    )
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
                categoryTitle = category?.title ?: stringResource(uiR.string.none),
                transactionStatus = transaction.status,
                ignored = transaction.ignored,
                onClick = { onTransactionClick(transaction.id) },
                modifier = Modifier.animateItem(),
            )
        }
    }
}

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
                        initialBalance = BigDecimal.ZERO,
                    ),
                    currentBalance = BigDecimal.valueOf(100),
                    availableCategories = categories,
                    selectedCategories = categories.take(3),
                    transactionsCategories = transactionsCategories,
                    financeSectionType = NONE,
                    dateType = DateType.ALL,
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
                        initialBalance = BigDecimal.ZERO,
                    ),
                    currentBalance = BigDecimal.valueOf(100),
                    availableCategories = categories,
                    selectedCategories = categories.take(2),
                    transactionsCategories = transactionsCategories,
                    financeSectionType = EXPENSES,
                    dateType = DateType.ALL,
                    shouldDisplayUndoTransaction = false,
                ),
                onWalletEvent = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}