package ru.resodostudios.cashsense.feature.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.Clock
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionCategoryCrossRef
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.feature.categories.CategoriesUiState
import ru.resodostudios.cashsense.feature.categories.CategoriesViewModel
import java.util.UUID
import ru.resodostudios.cashsense.core.ui.R as uiR
import ru.resodostudios.cashsense.feature.categories.R as categoriesR

@Composable
fun TransactionDialog(
    onDismiss: () -> Unit,
    walletId: Long = 0,
    transactionViewModel: TransactionViewModel = hiltViewModel(),
    categoriesViewModel: CategoriesViewModel = hiltViewModel()
) {
    val categoriesState by categoriesViewModel.categoriesUiState.collectAsStateWithLifecycle()

    TransactionDialog(
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

@Composable
fun TransactionDialog(
    categoriesState: CategoriesUiState,
    walletId: Long = 0,
    onDismiss: () -> Unit,
    onConfirm: (Transaction) -> Unit
) {
    var amount by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }

    var categoryId: Long? = null

    when (categoriesState) {
        CategoriesUiState.Loading -> LoadingState()
        is CategoriesUiState.Success ->
            AlertDialog(
                onDismissRequest = onDismiss,
                icon = { Icon(CsIcons.Transaction, contentDescription = null) },
                title = { Text(text = stringResource(R.string.new_transaction)) },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TextField(
                            value = amount,
                            onValueChange = { amount = it },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            label = { Text(text = stringResource(R.string.amount)) },
                            maxLines = 1
                        )
                        TextField(
                            value = category,
                            onValueChange = { },
                            trailingIcon = {
                                CategoryDropDownMenu(
                                    categories = categoriesState.categories,
                                    onCategoryClick = {
                                        category = it.title.toString()
                                        categoryId = it.categoryId
                                    },
                                    onCategoryCreate = {

                                    }
                                )
                            },
                            label = { Text(text = stringResource(categoriesR.string.category)) },
                            readOnly = true,
                            maxLines = 1,
                            placeholder = { Text(text = stringResource(uiR.string.none)) }
                        )
                        TextField(
                            value = description,
                            onValueChange = { description = it },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text
                            ),
                            label = { Text(text = stringResource(uiR.string.description)) },
                            maxLines = 1,
                            supportingText = { Text(text = stringResource(uiR.string.optional)) }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
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
                        }
                    ) {
                        Text(text = stringResource(uiR.string.save))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(text = stringResource(uiR.string.cancel))
                    }
                }
            )
    }
}

@Composable
private fun CategoryDropDownMenu(
    categories: List<Category>,
    onCategoryClick: (Category) -> Unit,
    onCategoryCreate: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopStart)
    ) {
        IconButton(onClick = { expanded = true }) {
            Icon(CsIcons.DropDown, contentDescription = null)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(text = "Create") },
                leadingIcon = {
                    Icon(imageVector = CsIcons.Add, contentDescription = null)
                },
                onClick = {
                    onCategoryCreate()
                    expanded = false
                }
            )
            for (category in categories) {
                DropdownMenuItem(
                    text = { Text(text = category.title.toString()) },
                    onClick = {
                        onCategoryClick(category)
                        expanded = false
                    }
                )
            }
        }
    }
}