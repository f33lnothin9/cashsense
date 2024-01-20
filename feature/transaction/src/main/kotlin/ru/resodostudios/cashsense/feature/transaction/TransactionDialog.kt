package ru.resodostudios.cashsense.feature.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.Clock
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionCategoryCrossRef
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.validateAmount
import ru.resodostudios.cashsense.feature.categories.CategoriesUiState
import ru.resodostudios.cashsense.feature.categories.CategoriesViewModel
import java.util.UUID
import ru.resodostudios.cashsense.core.designsystem.R as designsystemR
import ru.resodostudios.cashsense.core.ui.R as uiR
import ru.resodostudios.cashsense.feature.categories.R as categoriesR

@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    walletId: Long = 0,
    transactionViewModel: TransactionViewModel = hiltViewModel(),
    categoriesViewModel: CategoriesViewModel = hiltViewModel()
) {
    val categoriesState by categoriesViewModel.categoriesUiState.collectAsStateWithLifecycle()
    AddTransactionDialog(
        categoriesState = categoriesState,
        walletId = walletId,
        onDismiss = onDismiss,
        onConfirm = {
            transactionViewModel.upsertTransaction(it)
            if (it.categoryOwnerId != null) {
                transactionViewModel.upsertTransactionCategoryCrossRef(
                    TransactionCategoryCrossRef(
                        transactionId = it.transactionId,
                        categoryId = it.categoryOwnerId!!
                    )
                )
            }
            onDismiss()
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddTransactionDialog(
    categoriesState: CategoriesUiState,
    walletId: Long = 0,
    onDismiss: () -> Unit,
    onConfirm: (Transaction) -> Unit
) {
    var amount by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf(Category()) }

    val (amountTextField, descriptionTextField) = remember { FocusRequester.createRefs() }

    when (categoriesState) {
        CategoriesUiState.Loading -> LoadingState()
        is CategoriesUiState.Success -> CsAlertDialog(
            titleRes = R.string.new_transaction,
            confirmButtonTextRes = uiR.string.add,
            dismissButtonTextRes = uiR.string.core_ui_cancel,
            icon = CsIcons.Transaction,
            onConfirm = {
                onConfirm(
                    Transaction(
                        transactionId = UUID.randomUUID(),
                        walletOwnerId = walletId,
                        categoryOwnerId = category.categoryId,
                        description = description,
                        amount = amount.toDouble(),
                        date = Clock.System.now()
                    )
                )
            },
            isConfirmEnabled = amount.validateAmount().second,
            onDismiss = onDismiss
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.validateAmount().first },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    label = { Text(text = stringResource(uiR.string.amount)) },
                    placeholder = { Text(text = stringResource(uiR.string.amount) + "*") },
                    supportingText = { Text(text = stringResource(uiR.string.required)) },
                    maxLines = 1,
                    modifier = Modifier
                        .focusRequester(amountTextField)
                        .focusProperties { next = descriptionTextField }
                )
                CategoryExposedDropdownMenuBox(
                    title = category.title ?: stringResource(uiR.string.none),
                    icon = designsystemR.drawable.ic_outlined_category,
                    categories = categoriesState.categories,
                    onCategoryClick = { category = it }
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    label = { Text(text = stringResource(uiR.string.description)) },
                    maxLines = 1,
                    modifier = Modifier.focusRequester(descriptionTextField)
                )
            }
            LaunchedEffect(Unit) {
                amountTextField.requestFocus()
            }
        }
    }
}

@Composable
fun EditTransactionDialog(
    transactionWithCategory: TransactionWithCategory,
    onDismiss: () -> Unit,
    transactionViewModel: TransactionViewModel = hiltViewModel(),
    categoriesViewModel: CategoriesViewModel = hiltViewModel()
) {
    val categoriesState by categoriesViewModel.categoriesUiState.collectAsStateWithLifecycle()
    EditTransactionDialog(
        categoriesState = categoriesState,
        transactionWithCategory = transactionWithCategory,
        onDismiss = onDismiss,
        onConfirm = {
            transactionViewModel.upsertTransaction(it)
            if (it.categoryOwnerId != null) {
                transactionViewModel.deleteTransactionCategoryCrossRef(it.transactionId)
                transactionViewModel.upsertTransactionCategoryCrossRef(
                    TransactionCategoryCrossRef(
                        transactionId = it.transactionId,
                        categoryId = it.categoryOwnerId!!
                    )
                )
            }
            onDismiss()
        }
    )
}

@Composable
fun EditTransactionDialog(
    transactionWithCategory: TransactionWithCategory,
    categoriesState: CategoriesUiState,
    onDismiss: () -> Unit,
    onConfirm: (Transaction) -> Unit
) {
    var amount by rememberSaveable { mutableStateOf(transactionWithCategory.transaction.amount.toString()) }
    var description by rememberSaveable { mutableStateOf(transactionWithCategory.transaction.description) }
    var category by rememberSaveable { mutableStateOf(transactionWithCategory.category) }

    when (categoriesState) {
        CategoriesUiState.Loading -> LoadingState()
        is CategoriesUiState.Success -> CsAlertDialog(
            titleRes = R.string.edit_transaction,
            confirmButtonTextRes = uiR.string.save,
            dismissButtonTextRes = uiR.string.core_ui_cancel,
            icon = CsIcons.Transaction,
            onConfirm = {
                onConfirm(
                    Transaction(
                        transactionId = transactionWithCategory.transaction.transactionId,
                        walletOwnerId = transactionWithCategory.transaction.walletOwnerId,
                        categoryOwnerId = category?.categoryId,
                        description = description,
                        amount = amount.toDouble(),
                        date = transactionWithCategory.transaction.date
                    )
                )
            },
            isConfirmEnabled = amount.validateAmount().second,
            onDismiss = onDismiss
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.validateAmount().first },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    label = { Text(text = stringResource(uiR.string.amount)) },
                    placeholder = { Text(text = stringResource(uiR.string.amount) + "*") },
                    supportingText = { Text(text = stringResource(uiR.string.required)) },
                    maxLines = 1
                )
                CategoryExposedDropdownMenuBox(
                    title = category?.title ?: stringResource(uiR.string.none),
                    icon = category?.icon ?: designsystemR.drawable.ic_outlined_category,
                    categories = categoriesState.categories,
                    onCategoryClick = { category = it }
                )
                OutlinedTextField(
                    value = description.toString(),
                    onValueChange = { description = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    label = { Text(text = stringResource(uiR.string.description)) },
                    maxLines = 1
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryExposedDropdownMenuBox(
    title: String,
    icon: Int,
    categories: List<Category>,
    onCategoryClick: (Category) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var iconId by rememberSaveable { mutableIntStateOf(icon) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = title,
            onValueChange = {},
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
            label = { Text(text = stringResource(categoriesR.string.category)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            placeholder = { Text(text = stringResource(uiR.string.none)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(iconId),
                    contentDescription = null
                )
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.title.toString()) },
                    onClick = {
                        onCategoryClick(category)
                        iconId = category.icon!!
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(category.icon!!),
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}