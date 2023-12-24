package ru.resodostudios.cashsense.feature.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
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
import ru.resodostudios.cashsense.feature.categories.CategoriesUiState
import ru.resodostudios.cashsense.feature.categories.CategoriesViewModel
import java.util.UUID
import ru.resodostudios.cashsense.core.ui.R as uiR
import ru.resodostudios.cashsense.feature.categories.R as categoriesR

@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    walletId: Long = 0,
    transactionViewModel: TransactionViewModel = hiltViewModel(),
    categoriesViewModel: CategoriesViewModel = hiltViewModel()
) {
    val categoriesState by categoriesViewModel.categoriesUiState.collectAsStateWithLifecycle(initialValue = CategoriesUiState.Loading)

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
    var category by rememberSaveable { mutableStateOf("") }

    val (amountTextField, descriptionTextField) = remember { FocusRequester.createRefs() }

    var categoryId: Long? = null

    when (categoriesState) {
        CategoriesUiState.Loading -> LoadingState()
        is CategoriesUiState.Success -> CsAlertDialog(
            titleRes = R.string.new_transaction,
            confirmButtonTextRes = uiR.string.add,
            dismissButtonTextRes = uiR.string.cancel,
            icon = CsIcons.Transaction,
            onConfirm = {
                onConfirm(
                    Transaction(
                        transactionId = UUID.randomUUID(),
                        walletOwnerId = walletId,
                        categoryOwnerId = categoryId,
                        description = description,
                        amount = amount.toDouble(),
                        date = Clock.System.now()
                    )
                )
            },
            onDismiss = onDismiss
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        label = { Text(text = stringResource(R.string.amount)) },
                        maxLines = 1,
                        modifier = Modifier
                            .focusRequester(amountTextField)
                            .focusProperties { next = descriptionTextField }
                    )
                }
                item {
                    CategoryExposedDropdownMenuBox(
                        categoryName = category,
                        categories = categoriesState.categories,
                        onCategoryClick = {
                            category = it.title.toString()
                            categoryId = it.categoryId
                        },
                        onCategoryCreate = { TODO() }
                    )
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        label = { Text(text = stringResource(uiR.string.description)) },
                        maxLines = 1,
                        supportingText = { Text(text = stringResource(uiR.string.optional)) },
                        modifier = Modifier.focusRequester(descriptionTextField)
                    )
                }
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
    val categoriesState by categoriesViewModel.categoriesUiState.collectAsStateWithLifecycle(initialValue = CategoriesUiState.Loading)

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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditTransactionDialog(
    transactionWithCategory: TransactionWithCategory,
    categoriesState: CategoriesUiState,
    onDismiss: () -> Unit,
    onConfirm: (Transaction) -> Unit
) {
    var amount by rememberSaveable { mutableStateOf(transactionWithCategory.transaction.amount.toString()) }
    var description by rememberSaveable { mutableStateOf(transactionWithCategory.transaction.description) }
    var category by rememberSaveable { mutableStateOf(transactionWithCategory.category?.title) }

    val (amountTextField, descriptionTextField) = remember { FocusRequester.createRefs() }

    var categoryId: Long? = null

    when (categoriesState) {
        CategoriesUiState.Loading -> LoadingState()
        is CategoriesUiState.Success -> CsAlertDialog(
            titleRes = R.string.edit_transaction,
            confirmButtonTextRes = uiR.string.save,
            dismissButtonTextRes = uiR.string.cancel,
            icon = CsIcons.Transaction,
            onConfirm = {
                onConfirm(
                    Transaction(
                        transactionId = transactionWithCategory.transaction.transactionId,
                        walletOwnerId = transactionWithCategory.transaction.walletOwnerId,
                        categoryOwnerId = categoryId,
                        description = description,
                        amount = amount.toDouble(),
                        date = transactionWithCategory.transaction.date
                    )
                )
            },
            onDismiss = onDismiss
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        label = { Text(text = stringResource(R.string.amount)) },
                        maxLines = 1,
                        modifier = Modifier
                            .focusRequester(amountTextField)
                            .focusProperties { next = descriptionTextField }
                    )
                }
                item {
                    CategoryExposedDropdownMenuBox(
                        categoryName = if (category == null) stringResource(uiR.string.none) else category.toString(),
                        categories = categoriesState.categories,
                        onCategoryClick = {
                            category = it.title
                            categoryId = it.categoryId
                        },
                        onCategoryCreate = { TODO() }
                    )
                }
                item {
                    OutlinedTextField(
                        value = description.toString(),
                        onValueChange = { description = it },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        label = { Text(text = stringResource(uiR.string.description)) },
                        maxLines = 1,
                        supportingText = { Text(text = stringResource(uiR.string.optional)) },
                        modifier = Modifier.focusRequester(descriptionTextField)
                    )
                }
            }
            LaunchedEffect(Unit) {
                amountTextField.requestFocus()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryExposedDropdownMenuBox(
    categoryName: String,
    categories: List<Category>,
    onCategoryClick: (Category) -> Unit,
    onCategoryCreate: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = categoryName,
            onValueChange = {},
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
            label = { Text(text = stringResource(categoriesR.string.category)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            placeholder = { Text(text = stringResource(uiR.string.none)) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(uiR.string.add)) },
                leadingIcon = {
                    Icon(imageVector = CsIcons.Add, contentDescription = null)
                },
                onClick = {
                    onCategoryCreate()
                    expanded = false
                }
            )
            categories.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption.title.toString()) },
                    onClick = {
                        onCategoryClick(selectionOption)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}