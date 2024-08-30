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
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.Color
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
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.StatusType
import ru.resodostudios.cashsense.core.ui.CategoriesUiState
import ru.resodostudios.cashsense.core.ui.CategoriesUiState.Loading
import ru.resodostudios.cashsense.core.ui.CategoriesUiState.Success
import ru.resodostudios.cashsense.core.ui.DatePickerTextField
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.StoredIcon
import ru.resodostudios.cashsense.core.ui.formatDate
import ru.resodostudios.cashsense.core.ui.validateAmount
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.Save
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateAmount
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateCategory
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateDate
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateDescription
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateIgnoring
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateStatus
import ru.resodostudios.cashsense.feature.transaction.TransactionDialogEvent.UpdateTransactionType
import ru.resodostudios.cashsense.core.ui.R as uiR

@Composable
fun TransactionDialog(
    onDismiss: () -> Unit,
    viewModel: TransactionDialogViewModel = hiltViewModel(),
) {
    val transactionDialogState by viewModel.transactionDialogUiState.collectAsStateWithLifecycle()
    val categoriesState by viewModel.categoriesUiState.collectAsStateWithLifecycle()

    TransactionDialog(
        transactionDialogState = transactionDialogState,
        categoriesState = categoriesState,
        onDismiss = onDismiss,
        onTransactionEvent = viewModel::onTransactionEvent,
    )
}

@Composable
fun TransactionDialog(
    transactionDialogState: TransactionDialogUiState,
    categoriesState: CategoriesUiState,
    onDismiss: () -> Unit,
    onTransactionEvent: (TransactionDialogEvent) -> Unit,
) {
    val isTransactionLoading = transactionDialogState.isLoading
    val isCategoriesLoading = categoriesState is Loading

    val dialogTitle =
        if (transactionDialogState.transactionId.isNotEmpty()) R.string.feature_transaction_edit_transaction else R.string.feature_transaction_new_transaction
    val dialogConfirmText =
        if (transactionDialogState.transactionId.isNotEmpty()) uiR.string.core_ui_save else uiR.string.core_ui_add

    CsAlertDialog(
        titleRes = dialogTitle,
        confirmButtonTextRes = dialogConfirmText,
        dismissButtonTextRes = uiR.string.core_ui_cancel,
        iconRes = CsIcons.ReceiptLong,
        onConfirm = {
            onTransactionEvent(Save)
            onDismiss()
        },
        isConfirmEnabled = transactionDialogState.amount.validateAmount().second,
        onDismiss = onDismiss,
    ) {
        if (isTransactionLoading || isCategoriesLoading) {
            LoadingState(
                Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        } else {
            val (descTextField, amountTextField) = remember { FocusRequester.createRefs() }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.verticalScroll(rememberScrollState()),
            ) {
                TransactionTypeChoiceRow(
                    onTransactionEvent = onTransactionEvent,
                    transactionState = transactionDialogState,
                )
                OutlinedTextField(
                    value = transactionDialogState.amount,
                    onValueChange = { onTransactionEvent(UpdateAmount(it.validateAmount().first)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next,
                    ),
                    label = { Text(stringResource(uiR.string.core_ui_amount)) },
                    placeholder = { Text(stringResource(uiR.string.core_ui_amount) + "*") },
                    supportingText = { Text(stringResource(uiR.string.core_ui_required)) },
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(amountTextField)
                        .focusProperties { next = descTextField },
                )
                CategoryExposedDropdownMenuBox(
                    currentCategory = transactionDialogState.category,
                    categoriesState = categoriesState,
                    onCategoryClick = { onTransactionEvent(UpdateCategory(it)) },
                )
                TransactionStatusChoiceRow(
                    onTransactionEvent = onTransactionEvent,
                    transactionState = transactionDialogState,
                )
                DatePickerTextField(
                    value = transactionDialogState.date.formatDate(),
                    labelTextId = uiR.string.core_ui_date,
                    iconId = CsIcons.Calendar,
                    modifier = Modifier.fillMaxWidth(),
                    initialSelectedDateMillis = transactionDialogState.date.toEpochMilliseconds(),
                    onDateClick = { onTransactionEvent(UpdateDate(Instant.fromEpochMilliseconds(it))) },
                )
                OutlinedTextField(
                    value = transactionDialogState.description,
                    onValueChange = { onTransactionEvent(UpdateDescription(it)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done,
                    ),
                    label = { Text(stringResource(uiR.string.core_ui_description)) },
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(descTextField),
                )
                CsListItem(
                    headlineContent = { Text(stringResource(R.string.feature_transaction_ignore)) },
                    leadingContent = {
                        Icon(
                            imageVector = ImageVector.vectorResource(CsIcons.Block),
                            contentDescription = null,
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = transactionDialogState.ignored,
                            onCheckedChange = { onTransactionEvent(UpdateIgnoring(it)) },
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

@Composable
private fun TransactionTypeChoiceRow(
    onTransactionEvent: (TransactionDialogEvent) -> Unit,
    transactionState: TransactionDialogUiState,
) {
    val transactionTypes = listOf(
        stringResource(R.string.feature_transaction_expense),
        stringResource(R.string.feature_transaction_income),
    )
    val chartDirectionIcons = listOf(
        CsIcons.TrendingDown,
        CsIcons.TrendingUp,
    )
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth(),
    ) {
        transactionTypes.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = transactionTypes.size,
                ),
                onClick = { onTransactionEvent(UpdateTransactionType(TransactionType.entries[index])) },
                selected = transactionState.transactionType == TransactionType.entries[index],
                icon = {
                    SegmentedButtonDefaults.Icon(active = transactionState.transactionType == TransactionType.entries[index]) {
                        Icon(
                            imageVector = ImageVector.vectorResource(chartDirectionIcons[index]),
                            contentDescription = null,
                            modifier = Modifier.size(SegmentedButtonDefaults.IconSize),
                        )
                    }
                },
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

@Composable
private fun TransactionStatusChoiceRow(
    onTransactionEvent: (TransactionDialogEvent) -> Unit,
    transactionState: TransactionDialogUiState,
) {
    val statusTypes = listOf(
        stringResource(R.string.feature_transaction_status_completed),
        stringResource(R.string.feature_transaction_status_pending),
    )
    val statusIcons = listOf(
        CsIcons.CheckCircle,
        CsIcons.Pending,
    )
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth(),
    ) {
        statusTypes.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = statusTypes.size,
                ),
                onClick = { onTransactionEvent(UpdateStatus(StatusType.entries[index])) },
                selected = transactionState.status == StatusType.entries[index],
                icon = {
                    SegmentedButtonDefaults.Icon(active = transactionState.status == StatusType.entries[index]) {
                        Icon(
                            imageVector = ImageVector.vectorResource(statusIcons[index]),
                            contentDescription = null,
                            modifier = Modifier.size(SegmentedButtonDefaults.IconSize),
                        )
                    }
                },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryExposedDropdownMenuBox(
    currentCategory: Category?,
    categoriesState: CategoriesUiState,
    onCategoryClick: (Category) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var iconId by rememberSaveable { mutableIntStateOf(currentCategory?.iconId ?: 0) }

    when (categoriesState) {
        Loading -> Unit
        is Success -> {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryEditable),
                    readOnly = true,
                    value = currentCategory?.title ?: stringResource(uiR.string.core_ui_none),
                    onValueChange = {},
                    label = { Text(stringResource(R.string.feature_transaction_category_title)) },
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
                                text = stringResource(uiR.string.core_ui_none),
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
                    categoriesState.categories.forEach { category ->
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
    }
}