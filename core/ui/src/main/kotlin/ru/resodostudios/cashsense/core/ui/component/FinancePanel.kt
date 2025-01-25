package ru.resodostudios.cashsense.core.ui.component

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Month
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ChevronLeft
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.ChevronRight
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Close
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.DateType
import ru.resodostudios.cashsense.core.model.data.DateType.ALL
import ru.resodostudios.cashsense.core.model.data.DateType.MONTH
import ru.resodostudios.cashsense.core.model.data.DateType.WEEK
import ru.resodostudios.cashsense.core.model.data.DateType.YEAR
import ru.resodostudios.cashsense.core.model.data.FinanceType
import ru.resodostudios.cashsense.core.model.data.FinanceType.EXPENSES
import ru.resodostudios.cashsense.core.model.data.FinanceType.INCOME
import ru.resodostudios.cashsense.core.model.data.FinanceType.NOT_SET
import ru.resodostudios.cashsense.core.model.data.TransactionFilter
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.ui.TransactionCategoryPreviewParameterProvider
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.ui.util.getCurrentYear
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.math.MathContext
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Currency
import java.util.Locale
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FinancePanel(
    availableCategories: List<Category>,
    currency: Currency,
    expenses: BigDecimal,
    expensesProgress: Float,
    income: BigDecimal,
    incomeProgress: Float,
    graphData: Map<Int, BigDecimal>,
    transactionFilter: TransactionFilter,
    onDateTypeUpdate: (DateType) -> Unit,
    onFinanceTypeUpdate: (FinanceType) -> Unit,
    onSelectedDateUpdate: (Short) -> Unit,
    onCategorySelect: (Category) -> Unit,
    onCategoryDeselect: (Category) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SharedTransitionLayout {
            AnimatedContent(
                targetState = transactionFilter.financeType,
                label = "FinancePanel",
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
                            val animatedExpensesProgress by animateFloatAsState(
                                targetValue = expensesProgress,
                                label = "ExpensesProgress",
                                animationSpec = tween(durationMillis = 400),
                            )
                            val animatedIncomeProgress by animateFloatAsState(
                                targetValue = incomeProgress,
                                label = "IncomeProgress",
                                animationSpec = tween(durationMillis = 400),
                            )
                            FinanceCard(
                                title = expenses,
                                currency = currency,
                                supportingTextId = localesR.string.expenses,
                                indicatorProgress = animatedExpensesProgress,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    onFinanceTypeUpdate(EXPENSES)
                                    onDateTypeUpdate(MONTH)
                                },
                                animatedVisibilityScope = this@AnimatedContent,
                            )
                            FinanceCard(
                                title = income,
                                currency = currency,
                                supportingTextId = localesR.string.income_plural,
                                indicatorProgress = animatedIncomeProgress,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    onFinanceTypeUpdate(INCOME)
                                    onDateTypeUpdate(MONTH)
                                },
                                animatedVisibilityScope = this@AnimatedContent,
                            )
                        }
                    }

                    EXPENSES -> {
                        DetailedFinanceSection(
                            title = expenses,
                            graphData = graphData,
                            transactionFilter = transactionFilter,
                            currency = currency,
                            supportingTextId = localesR.string.expenses,
                            onBackClick = {
                                onFinanceTypeUpdate(NOT_SET)
                                onDateTypeUpdate(ALL)
                            },
                            onDateTypeUpdate = onDateTypeUpdate,
                            onSelectedDateUpdate = onSelectedDateUpdate,
                            onCategorySelect = onCategorySelect,
                            onCategoryDeselect = onCategoryDeselect,
                            modifier = Modifier.fillMaxWidth(),
                            animatedVisibilityScope = this@AnimatedContent,
                            availableCategories = availableCategories,
                        )
                    }

                    INCOME -> {
                        DetailedFinanceSection(
                            title = income,
                            graphData = graphData,
                            transactionFilter = transactionFilter,
                            currency = currency,
                            supportingTextId = localesR.string.income_plural,
                            onBackClick = {
                                onFinanceTypeUpdate(NOT_SET)
                                onDateTypeUpdate(ALL)
                            },
                            onDateTypeUpdate = onDateTypeUpdate,
                            onSelectedDateUpdate = onSelectedDateUpdate,
                            onCategorySelect = onCategorySelect,
                            onCategoryDeselect = onCategoryDeselect,
                            modifier = Modifier.fillMaxWidth(),
                            animatedVisibilityScope = this@AnimatedContent,
                            availableCategories = availableCategories,
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
                label = "FinanceCardTitle",
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
    onDateTypeUpdate: (DateType) -> Unit,
    onSelectedDateUpdate: (Short) -> Unit,
    onCategorySelect: (Category) -> Unit,
    onCategoryDeselect: (Category) -> Unit,
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
                onDateTypeUpdate = onDateTypeUpdate,
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
                onSelectedDateUpdate = onSelectedDateUpdate,
                transactionFilter = transactionFilter,
                modifier = Modifier.padding(bottom = 6.dp, start = 16.dp, end = 16.dp),
            )
        }
        AnimatedAmount(
            targetState = title,
            label = "DetailedFinanceCard",
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
                onCategorySelect = onCategorySelect,
                onCategoryDeselect = onCategoryDeselect,
                modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
            )
        }
    }
}

@Composable
private fun FilterDateTypeSelectorRow(
    transactionFilter: TransactionFilter,
    onDateTypeUpdate: (DateType) -> Unit,
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
                onClick = { onDateTypeUpdate(DateType.entries[index]) },
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
    onSelectedDateUpdate: (Short) -> Unit,
    transactionFilter: TransactionFilter,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth(),
    ) {
        IconButton(
            onClick = { onSelectedDateUpdate(-1) },
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
            onClick = { onSelectedDateUpdate(1) },
        ) {
            Icon(
                imageVector = CsIcons.Outlined.ChevronRight,
                contentDescription = null,
            )
        }
    }
}

fun getFinanceProgress(
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
                transactionFilter = TransactionFilter(
                    selectedCategories = categories.take(3).toSet(),
                    financeType = NOT_SET,
                    dateType = ALL,
                    selectedYearMonth = YearMonth.of(2025, 1),
                ),
                availableCategories = categories.toList(),
                currency = getUsdCurrency(),
                expenses = BigDecimal(200),
                expensesProgress = 0.2f,
                income = BigDecimal(800),
                incomeProgress = 0.8f,
                graphData = emptyMap(),
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                onDateTypeUpdate = {},
                onFinanceTypeUpdate = {},
                onSelectedDateUpdate = {},
                onCategorySelect = {},
                onCategoryDeselect = {},
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
                transactionFilter = TransactionFilter(
                    selectedCategories = categories.take(3).toSet(),
                    financeType = EXPENSES,
                    dateType = MONTH,
                    selectedYearMonth = YearMonth.of(2025, 1),
                ),
                availableCategories = categories.toList(),
                currency = getUsdCurrency(),
                expenses = BigDecimal(200),
                expensesProgress = 0f,
                income = BigDecimal(800),
                incomeProgress = 0f,
                graphData = mapOf(
                    1 to BigDecimal(100),
                    2 to BigDecimal(200),
                    3 to BigDecimal(300),
                    4 to BigDecimal(400),
                    5 to BigDecimal(500),
                ),
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                onDateTypeUpdate = {},
                onFinanceTypeUpdate = {},
                onSelectedDateUpdate = {},
                onCategorySelect = {},
                onCategoryDeselect = {},
            )
        }
    }
}