package ru.resodostudios.cashsense.feature.wallet.detail

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.StoredIcon
import ru.resodostudios.cashsense.core.ui.formatAmount
import ru.resodostudios.cashsense.core.ui.formatDate
import ru.resodostudios.cashsense.feature.transaction.TransactionBottomSheet
import ru.resodostudios.cashsense.feature.transaction.TransactionDialog
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogViewModel
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialog
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogViewModel
import java.math.BigDecimal
import java.math.MathContext
import java.time.temporal.ChronoUnit
import ru.resodostudios.cashsense.core.ui.R as uiR
import ru.resodostudios.cashsense.feature.transaction.R as transactionR

@Composable
internal fun WalletScreen(
    showDetailActions: Boolean,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
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
        onWalletDialogEvent = walletDialogViewModel::onWalletDialogEvent,
        onTransactionEvent = transactionDialogViewModel::onTransactionEvent,
        addToSelectedCategories = walletViewModel::addToSelectedCategories,
        removeFromSelectedCategories = walletViewModel::removeFromSelectedCategories,
        updateFinanceType = walletViewModel::updateFinanceType,
        updateDateType = walletViewModel::updateDateType,
        hideTransaction = walletViewModel::hideTransaction,
        undoTransactionRemoval = walletViewModel::undoTransactionRemoval,
        clearUndoState = walletViewModel::clearUndoState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WalletScreen(
    walletState: WalletUiState,
    showDetailActions: Boolean,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onWalletDialogEvent: (WalletDialogEvent) -> Unit,
    onTransactionEvent: (TransactionDialogEvent) -> Unit,
    addToSelectedCategories: (Category) -> Unit,
    removeFromSelectedCategories: (Category) -> Unit,
    updateFinanceType: (FinanceSectionType) -> Unit,
    updateDateType: (DateType) -> Unit,
    hideTransaction: (String) -> Unit = {},
    undoTransactionRemoval: () -> Unit = {},
    clearUndoState: () -> Unit = {},
) {
    var showWalletDialog by rememberSaveable { mutableStateOf(false) }

    var showTransactionBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showTransactionDialog by rememberSaveable { mutableStateOf(false) }
    
    when (walletState) {
        WalletUiState.Loading -> LoadingState(Modifier.fillMaxSize())
        is WalletUiState.Success -> {
            val transactionDeletedMessage = stringResource(transactionR.string.feature_transaction_deleted)
            val undoText = stringResource(uiR.string.core_ui_undo)

            LaunchedEffect(walletState.shouldDisplayUndoTransaction) {
                if (walletState.shouldDisplayUndoTransaction) {
                    val snackBarResult = onShowSnackbar(transactionDeletedMessage, undoText)
                    if (snackBarResult) {
                        undoTransactionRemoval()
                    } else {
                        clearUndoState()
                    }
                }
            }
            LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
                clearUndoState()
            }

            LazyColumn(Modifier.fillMaxSize()) {
                item {
                    TopAppBar(
                        title = {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = walletState.wallet.title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = walletState.currentBalance.formatAmount(walletState.wallet.currency),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                        },
                        navigationIcon = {
                            if (showDetailActions) {
                                IconButton(onClick = onBackClick) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(CsIcons.ArrowBack),
                                        contentDescription = null,
                                    )
                                }
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    onTransactionEvent(
                                        TransactionDialogEvent.UpdateWalletId(
                                            walletState.wallet.id
                                        )
                                    )
                                    onTransactionEvent(TransactionDialogEvent.UpdateId(""))
                                    showTransactionDialog = true
                                }
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(CsIcons.Add),
                                    contentDescription = stringResource(transactionR.string.feature_transaction_add_transaction_icon_description),
                                )
                            }
                            if (showDetailActions) {
                                IconButton(
                                    onClick = {
                                        onWalletDialogEvent(
                                            WalletDialogEvent.UpdateId(walletState.wallet.id)
                                        )
                                        showWalletDialog = true
                                    },
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
                item {
                    FinancePanel(
                        walletState = walletState,
                        addToSelectedCategories = addToSelectedCategories,
                        removeFromSelectedCategories = removeFromSelectedCategories,
                        updateFinanceType = updateFinanceType,
                        updateDateType = updateDateType,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                    )
                }
                if (walletState.transactionsCategories.isNotEmpty()) {
                    transactions(
                        transactionsCategories = walletState.transactionsCategories,
                        currency = walletState.wallet.currency,
                        onTransactionClick = {
                            onTransactionEvent(TransactionDialogEvent.UpdateWalletId(walletState.wallet.id))
                            onTransactionEvent(TransactionDialogEvent.UpdateId(it))
                            onTransactionEvent(TransactionDialogEvent.UpdateCurrency(walletState.wallet.currency))
                            showTransactionBottomSheet = true
                        },
                    )
                } else {
                    item {
                        EmptyState(
                            messageRes = transactionR.string.feature_transaction_transactions_empty,
                            animationRes = transactionR.raw.anim_transactions_empty,
                            modifier = Modifier.fillParentMaxHeight(0.7f),
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
                    onDelete = hideTransaction,
                )
            }
            if (showTransactionDialog) {
                TransactionDialog(onDismiss = { showTransactionDialog = false })
            }
        }
    }
}

@Composable
private fun FinancePanel(
    walletState: WalletUiState,
    addToSelectedCategories: (Category) -> Unit,
    removeFromSelectedCategories: (Category) -> Unit,
    updateFinanceType: (FinanceSectionType) -> Unit,
    updateDateType: (DateType) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (walletState) {
        WalletUiState.Loading -> Unit
        is WalletUiState.Success -> {
            val expenses = walletState.transactionsCategories
                .asSequence()
                .filter { it.transaction.amount < BigDecimal.ZERO }
                .sumOf { it.transaction.amount.abs() }
            val expensesAnimated by animateFloatAsState(
                targetValue = expenses.toFloat(),
                label = "expensesAnimation",
                animationSpec = tween(durationMillis = 400),
            )
            val income = walletState.transactionsCategories
                .asSequence()
                .filter { it.transaction.amount > BigDecimal.ZERO }
                .sumOf { it.transaction.amount }
            val incomeAnimated by animateFloatAsState(
                targetValue = income.toFloat(),
                label = "incomeAnimation",
                animationSpec = tween(durationMillis = 400),
            )
            val expensesProgress by animateFloatAsState(
                targetValue = if (walletState.transactionsCategories.isNotEmpty()) expenses
                    .divide(
                        walletState.transactionsCategories.sumOf { it.transaction.amount.abs() },
                        MathContext.DECIMAL32,
                    )
                    .toFloat() else 0f,
                label = "expensesProgressAnimation",
                animationSpec = tween(durationMillis = 400),
            )

            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AnimatedContent(
                    targetState = walletState.financeSectionType,
                    label = "financePanel",
                    modifier = Modifier.animateContentSize(),
                ) { financeType ->
                    when (financeType) {
                        FinanceSectionType.NONE -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                FinanceCard(
                                    title = expensesAnimated
                                        .toBigDecimal()
                                        .formatAmount(walletState.wallet.currency),
                                    supportingTextId = R.string.feature_wallet_detail_expenses,
                                    indicatorProgress = expensesProgress,
                                    modifier = Modifier.weight(1f),
                                    onClick = { updateFinanceType(FinanceSectionType.EXPENSES) },
                                    enabled = expenses != BigDecimal.ZERO,
                                )
                                FinanceCard(
                                    title = incomeAnimated
                                        .toBigDecimal()
                                        .formatAmount(walletState.wallet.currency),
                                    supportingTextId = R.string.feature_wallet_detail_income,
                                    indicatorProgress = 1.0f - expensesProgress,
                                    modifier = Modifier.weight(1f),
                                    onClick = { updateFinanceType(FinanceSectionType.INCOME) },
                                    enabled = income != BigDecimal.ZERO,
                                )
                            }
                        }

                        FinanceSectionType.EXPENSES -> {
                            DetailedFinanceCard(
                                title = expensesAnimated
                                    .toBigDecimal()
                                    .formatAmount(walletState.wallet.currency),
                                supportingTextId = R.string.feature_wallet_detail_expenses,
                                availableCategories = walletState.availableCategories,
                                selectedCategories = walletState.selectedCategories,
                                onBackClick = { updateFinanceType(FinanceSectionType.NONE) },
                                addToSelectedCategories = addToSelectedCategories,
                                removeFromSelectedCategories = removeFromSelectedCategories,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }

                        FinanceSectionType.INCOME -> {
                            DetailedFinanceCard(
                                title = incomeAnimated
                                    .toBigDecimal()
                                    .formatAmount(walletState.wallet.currency),
                                supportingTextId = R.string.feature_wallet_detail_income,
                                availableCategories = walletState.availableCategories,
                                selectedCategories = walletState.selectedCategories,
                                onBackClick = { updateFinanceType(FinanceSectionType.NONE) },
                                addToSelectedCategories = addToSelectedCategories,
                                removeFromSelectedCategories = removeFromSelectedCategories,
                                modifier = Modifier.fillMaxWidth(),
                            )
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
                            onClick = { updateDateType(DateType.entries[index]) },
                            selected = walletState.dateType == DateType.entries[index],
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

@Composable
private fun FinanceCard(
    title: String,
    @StringRes
    supportingTextId: Int,
    indicatorProgress: Float,
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
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
            Text(
                text = stringResource(supportingTextId),
                style = MaterialTheme.typography.labelMedium,
            )
            LinearProgressIndicator(
                progress = { if (enabled) indicatorProgress else 0f },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun DetailedFinanceCard(
    title: String,
    @StringRes
    supportingTextId: Int,
    availableCategories: List<Category>,
    selectedCategories: List<Category>,
    onBackClick: () -> Unit,
    addToSelectedCategories: (Category) -> Unit,
    removeFromSelectedCategories: (Category) -> Unit,
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
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
            )
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(CsIcons.Close),
                    contentDescription = null,
                )
            }
        }
        Text(
            text = stringResource(supportingTextId),
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.labelLarge,
        )
        CategoryFilterPanel(
            availableCategories = availableCategories,
            selectedCategories = selectedCategories,
            addToSelectedCategories = addToSelectedCategories,
            removeFromSelectedCategories = removeFromSelectedCategories,
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp, end = 16.dp, top = 16.dp),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryFilterPanel(
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
            FilterChip(
                selected = selected,
                onClick = {
                    if (selectedCategories.contains(category)) {
                        removeFromSelectedCategories(category)
                    } else {
                        addToSelectedCategories(category)
                    }
                },
                label = { Text(category.title.toString()) },
                leadingIcon = {
                    Icon(
                        imageVector = if (selected) {
                            ImageVector.vectorResource(CsIcons.Confirm)
                        } else {
                            ImageVector.vectorResource(
                                StoredIcon.asRes(
                                    category.iconId ?: StoredIcon.CATEGORY.storedId
                                )
                            )
                        },
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                    )
                }
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
                    .animateItem()
                    .padding(start = 16.dp, top = 16.dp),
            )
        }
        item { Spacer(Modifier.height(16.dp)) }
        items(
            items = group.second,
            key = { it.transaction.id },
            contentType = { "transactionCategory" },
        ) { transactionCategory ->
            val category = transactionCategory.category

            CsListItem(
                headlineContent = {
                    Text(
                        text = transactionCategory.transaction.amount.formatAmount(
                            currency = currency,
                            withPlus = true,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                supportingContent = {
                    Text(
                        text = category?.title
                            ?: stringResource(ru.resodostudios.cashsense.core.ui.R.string.none),
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
                modifier = Modifier.animateItem(),
                onClick = {
                    onTransactionClick(transactionCategory.transaction.id)
                },
            )
        }
    }
}