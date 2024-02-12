package ru.resodostudios.cashsense.feature.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.validateAmount
import ru.resodostudios.cashsense.feature.categories.CategoriesUiState
import ru.resodostudios.cashsense.feature.categories.CategoriesViewModel
import ru.resodostudios.cashsense.core.designsystem.R as designsystemR
import ru.resodostudios.cashsense.core.ui.R as uiR

@Composable
internal fun TransactionRoute(
    onBackClick: () -> Unit,
    transactionViewModel: TransactionViewModel = hiltViewModel(),
    categoriesViewModel: CategoriesViewModel = hiltViewModel(),
) {
    val transactionState by transactionViewModel.transactionUiState.collectAsStateWithLifecycle()
    val categoriesState by categoriesViewModel.categoriesUiState.collectAsStateWithLifecycle()

    TransactionScreen(
        transactionState = transactionState,
        categoriesState = categoriesState,
        onTransactionEvent = transactionViewModel::onTransactionEvent,
        onBackClick = onBackClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TransactionScreen(
    transactionState: TransactionUiState,
    categoriesState: CategoriesUiState,
    onTransactionEvent: (TransactionEvent) -> Unit,
    onBackClick: () -> Unit,
) {
    when (categoriesState) {
        CategoriesUiState.Loading -> LoadingState()
        is CategoriesUiState.Success -> {
            Scaffold(
                topBar = {
                    val titleRes = if (transactionState.isEditing) R.string.feature_transaction_edit_transaction else R.string.feature_transaction_new_transaction

                    TopAppBar(
                        title = { Text(text = stringResource(titleRes)) },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(CsIcons.ArrowBack),
                                    contentDescription = null
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    onTransactionEvent(TransactionEvent.Confirm)
                                    onBackClick()
                                },
                                enabled = transactionState.amount.validateAmount().second,
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(CsIcons.Confirm),
                                    contentDescription = stringResource(R.string.feature_transaction_add_transaction_icon_description)
                                )
                            }
                        }
                    )
                }
            ) { paddingValues ->
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(150.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = paddingValues,
                    modifier = Modifier.padding(16.dp),
                ) {
                    item {
                        OutlinedTextField(
                            value = transactionState.amount,
                            onValueChange = { onTransactionEvent(TransactionEvent.UpdateAmount(it.validateAmount().first)) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                            ),
                            label = { Text(text = stringResource(uiR.string.amount)) },
                            placeholder = { Text(text = stringResource(uiR.string.amount) + "*") },
                            supportingText = { Text(text = stringResource(uiR.string.required)) },
                            maxLines = 1,
                            singleLine = true,
                        )
                    }
                    item {
                        CategoryExposedDropdownMenuBox(
                            currentCategory = transactionState.category,
                            categories = categoriesState.categories,
                            onCategoryClick = { onTransactionEvent(TransactionEvent.UpdateCategory(it)) },
                        )
                    }
                    item(
                        span = { GridItemSpan(2) }
                    ) {
                        OutlinedTextField(
                            value = transactionState.description,
                            onValueChange = { onTransactionEvent(TransactionEvent.UpdateDescription(it)) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text
                            ),
                            label = { Text(text = stringResource(uiR.string.description)) },
                            maxLines = 1,
                            singleLine = true,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryExposedDropdownMenuBox(
    currentCategory: Category?,
    categories: List<Category>,
    onCategoryClick: (Category) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var iconId by rememberSaveable {
        mutableIntStateOf(currentCategory?.iconRes ?: designsystemR.drawable.ic_outlined_category)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = currentCategory?.title ?: stringResource(uiR.string.none),
            onValueChange = {},
            label = { Text(text = stringResource(ru.resodostudios.cashsense.feature.category.R.string.feature_category_title)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(iconId),
                    contentDescription = null
                )
            },
            maxLines = 1,
            singleLine = true,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
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
                        iconId = category.iconRes!!
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(category.iconRes!!),
                            contentDescription = null
                        )
                    },
                )
            }
        }
    }
}