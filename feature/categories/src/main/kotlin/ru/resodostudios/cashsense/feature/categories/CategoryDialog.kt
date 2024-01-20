package ru.resodostudios.cashsense.feature.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.designsystem.R as designsystemR
import ru.resodostudios.cashsense.core.ui.R as uiR

@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    viewModel: CategoriesViewModel = hiltViewModel()
) {
    AddCategoryDialog(
        onDismiss = onDismiss,
        onConfirm = {
            viewModel.upsertCategory(it)
            onDismiss()
        }
    )
}

@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (Category) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var iconId by rememberSaveable { mutableIntStateOf(CsIcons.Category) }

    val titleTextField = remember { FocusRequester() }

    CsAlertDialog(
        titleRes = R.string.new_category,
        confirmButtonTextRes = uiR.string.add,
        dismissButtonTextRes = uiR.string.core_ui_cancel,
        icon = Icons.Outlined.Category,
        onConfirm = {
            onConfirm(
                Category(
                    title = title,
                    icon = iconId
                )
            )
        },
        isConfirmEnabled = title.isNotBlank(),
        onDismiss = onDismiss
    ) {
        val focusManager = LocalFocusManager.current
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                label = { Text(text = stringResource(uiR.string.core_ui_icon) + " & " + stringResource(uiR.string.title)) },
                placeholder = { Text(text = stringResource(uiR.string.title) + "*") },
                supportingText = { Text(text = stringResource(uiR.string.required)) },
                maxLines = 1,
                leadingIcon = {
                    IconPickerDropdownMenu(
                        currentIconId = iconId,
                        onIconClick = {
                            iconId = it
                            focusManager.clearFocus()
                        }
                    )
                },
                modifier = Modifier.focusRequester(titleTextField)
            )
        }
        LaunchedEffect(Unit) {
            titleTextField.requestFocus()
        }
    }
}

@Composable
fun EditCategoryDialog(
    category: Category,
    onDismiss: () -> Unit,
    viewModel: CategoriesViewModel = hiltViewModel()
) {
    EditCategoryDialog(
        category = category,
        onDismiss = onDismiss,
        onConfirm = {
            viewModel.upsertCategory(it)
            onDismiss()
        }
    )
}

@Composable
fun EditCategoryDialog(
    category: Category,
    onDismiss: () -> Unit,
    onConfirm: (Category) -> Unit
) {
    var title by rememberSaveable { mutableStateOf(category.title) }
    var iconId by rememberSaveable { mutableIntStateOf(category.icon!!) }

    CsAlertDialog(
        titleRes = R.string.feature_categories_edit_category,
        confirmButtonTextRes = uiR.string.save,
        dismissButtonTextRes = uiR.string.core_ui_cancel,
        icon = Icons.Outlined.Category,
        onConfirm = {
            onConfirm(
                Category(
                    categoryId = category.categoryId,
                    title = title,
                    icon = iconId
                )
            )
        },
        isConfirmEnabled = title?.isNotBlank() ?: false,
        onDismiss = onDismiss
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = title.toString(),
                onValueChange = { title = it },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                label = { Text(text = stringResource(uiR.string.core_ui_icon) + " & " + stringResource(uiR.string.title)) },
                placeholder = { Text(text = stringResource(uiR.string.title) + "*") },
                supportingText = { Text(text = stringResource(uiR.string.required)) },
                maxLines = 1,
                leadingIcon = {
                    IconPickerDropdownMenu(
                        currentIconId = iconId,
                        onIconClick = { iconId = it }
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IconPickerDropdownMenu(
    currentIconId: Int,
    onIconClick: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val icons = listOf(
        designsystemR.drawable.ic_outlined_category,
        R.drawable.ic_outlined_account_balance,
        R.drawable.ic_outlined_apparel,
        R.drawable.ic_outlined_chair,
        R.drawable.ic_outlined_exercise,
        R.drawable.ic_outlined_fastfood,
        R.drawable.ic_outlined_directions_bus,
        R.drawable.ic_outlined_handyman,
        R.drawable.ic_outlined_language,
        R.drawable.ic_outlined_local_bar,
        R.drawable.ic_outlined_local_gas_station,
        R.drawable.ic_outlined_memory,
        R.drawable.ic_outlined_payments,
        R.drawable.ic_outlined_pets,
        R.drawable.ic_outlined_phishing,
        R.drawable.ic_outlined_pill,
        R.drawable.ic_outlined_receipt_long,
        R.drawable.ic_outlined_restaurant,
        R.drawable.ic_outlined_school,
        R.drawable.ic_outlined_self_care,
        R.drawable.ic_outlined_shopping_cart,
        R.drawable.ic_outlined_sim_card,
        R.drawable.ic_outlined_smoking_rooms,
        R.drawable.ic_outlined_sports_esports,
        R.drawable.ic_outlined_travel
    )

    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(onClick = { expanded = true }) {
            Icon(painter = painterResource(currentIconId), contentDescription = null)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            FlowRow(
                maxItemsInEachRow = 5
            ) {
                icons.forEach { icon ->
                    IconButton(
                        onClick = {
                            onIconClick(icon)
                            expanded = false
                        }
                    ) {
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}