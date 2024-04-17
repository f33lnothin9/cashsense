package ru.resodostudios.cashsense.feature.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.Instant
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.ui.DatePickerTextField
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.StoredIcon
import ru.resodostudios.cashsense.core.ui.formatDate
import ru.resodostudios.cashsense.core.ui.validateAmount
import ru.resodostudios.cashsense.feature.category.list.CategoriesUiState
import ru.resodostudios.cashsense.feature.category.list.CategoriesViewModel
import ru.resodostudios.cashsense.core.ui.R as uiR
import ru.resodostudios.cashsense.feature.category.dialog.R as categoryDialogR

@Composable
fun TransactionDialog(
    onDismiss: () -> Unit,
    transactionDialogViewModel: TransactionDialogViewModel = hiltViewModel(),
    categoriesViewModel: CategoriesViewModel = hiltViewModel(),
) {
    val transactionDialogState by transactionDialogViewModel.transactionDialogUiState.collectAsStateWithLifecycle()
    val categoriesState by categoriesViewModel.categoriesUiState.collectAsStateWithLifecycle()

    TransactionDialog(
        transactionDialogState = transactionDialogState,
        categoriesState = categoriesState,
        onDismiss = onDismiss,
        onTransactionEvent = transactionDialogViewModel::onTransactionEvent,
    )
}

@Composable
fun TransactionDialog(
    transactionDialogState: TransactionDialogUiState,
    categoriesState: CategoriesUiState,
    onDismiss: () -> Unit,
    onTransactionEvent: (TransactionDialogEvent) -> Unit,
) {
    val dialogTitle = if (transactionDialogState.transactionId.isNotEmpty()) R.string.feature_transaction_edit_transaction else R.string.feature_transaction_new_transaction
    val dialogConfirmText = if (transactionDialogState.transactionId.isNotEmpty()) uiR.string.save else uiR.string.add

    CsAlertDialog(
        titleRes = dialogTitle,
        confirmButtonTextRes = dialogConfirmText,
        dismissButtonTextRes = uiR.string.core_ui_cancel,
        iconRes = CsIcons.Transaction,
        onConfirm = {
            onTransactionEvent(TransactionDialogEvent.Save)
            onDismiss()
        },
        isConfirmEnabled = transactionDialogState.amount.validateAmount().second,
        onDismiss = onDismiss,
    ) {
        when (categoriesState) {
            CategoriesUiState.Loading -> LoadingState(
                Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            )
            is CategoriesUiState.Success -> {
                val (descTextField, amountTextField) = remember { FocusRequester.createRefs() }

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                ) {
                    OutlinedTextField(
                        value = transactionDialogState.amount,
                        onValueChange = {
                            onTransactionEvent(
                                TransactionDialogEvent.UpdateAmount(it.validateAmount().first)
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next,
                        ),
                        label = { Text(stringResource(uiR.string.amount)) },
                        placeholder = { Text(stringResource(uiR.string.amount) + "*") },
                        supportingText = { Text(stringResource(uiR.string.required)) },
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(amountTextField)
                            .focusProperties { next = descTextField },
                    )
                    FinancialTypeChoiceRow(
                        onTransactionEvent = onTransactionEvent,
                        transactionState = transactionDialogState,
                    )
                    CategoryExposedDropdownMenuBox(
                        currentCategory = transactionDialogState.category,
                        categories = categoriesState.categories,
                        onCategoryClick = {
                            onTransactionEvent(
                                TransactionDialogEvent.UpdateCategory(it)
                            )
                        },
                    )
                    OutlinedTextField(
                        value = transactionDialogState.description,
                        onValueChange = {
                            onTransactionEvent(
                                TransactionDialogEvent.UpdateDescription(it)
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done,
                        ),
                        label = { Text(stringResource(uiR.string.description)) },
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(descTextField),
                    )
                    DatePickerTextField(
                        value = transactionDialogState.date.formatDate(),
                        labelTextId = uiR.string.core_ui_date,
                        iconId = CsIcons.Calendar,
                        modifier = Modifier.fillMaxWidth(),
                        initialSelectedDateMillis = transactionDialogState.date.toEpochMilliseconds(),
                        onDateClick = {
                            onTransactionEvent(
                                TransactionDialogEvent.UpdateDate(
                                    Instant.fromEpochMilliseconds(it)
                                )
                            )
                        },
                    )
                }
                LaunchedEffect(Unit) {
                    if (transactionDialogState.transactionId.isEmpty()) {
                        amountTextField.requestFocus()
                    }
                }
            }
        }
    }
}

@Composable
private fun FinancialTypeChoiceRow(
    onTransactionEvent: (TransactionDialogEvent) -> Unit,
    transactionState: TransactionDialogUiState,
) {
    val financialTypes = listOf(
        stringResource(R.string.feature_transaction_expense),
        stringResource(R.string.feature_transaction_income),
    )
    val financialIcons = listOf(
        CsIcons.TrendingDown,
        CsIcons.TrendingUp,
    )
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        financialTypes.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = financialTypes.size,
                ),
                onClick = {
                    onTransactionEvent(
                        TransactionDialogEvent.UpdateFinancialType(FinancialType.entries[index])
                    )
                },
                selected = transactionState.financialType == FinancialType.entries[index],
                icon = {
                    SegmentedButtonDefaults.Icon(active = transactionState.financialType == FinancialType.entries[index]) {
                        Icon(
                            imageVector = ImageVector.vectorResource(financialIcons[index]),
                            contentDescription = null,
                            modifier = Modifier.size(SegmentedButtonDefaults.IconSize),
                        )
                    }
                }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryExposedDropdownMenuBox(
    currentCategory: Category?,
    categories: List<Category>,
    onCategoryClick: (Category) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var iconId by rememberSaveable { mutableIntStateOf(currentCategory?.iconId ?: 0) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryEditable),
            readOnly = true,
            value = currentCategory?.title ?: stringResource(uiR.string.none),
            onValueChange = {},
            label = { Text(stringResource(categoryDialogR.string.feature_category_dialog_title)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(StoredIcon.asRes(iconId)),
                    contentDescription = null,
                )
            },
            maxLines = 1,
            singleLine = true,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(uiR.string.none),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                onClick = {
                    onCategoryClick(Category())
                    iconId = 0
                    expanded = false
                },
                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(CsIcons.Category),
                        contentDescription = null
                    )
                },
            )
            categories.forEach { category ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = category.title.toString(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    onClick = {
                        onCategoryClick(category)
                        iconId = category.iconId ?: 0
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(
                                StoredIcon.asRes(category.iconId ?: 0)
                            ),
                            contentDescription = null,
                        )
                    },
                )
            }
        }
    }
}