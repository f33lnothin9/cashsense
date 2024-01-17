package ru.resodostudios.cashsense.feature.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.SportsEsports
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.model.data.Category
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
    var icon by rememberSaveable { mutableStateOf(Icons.Outlined.Category.name.split(".")[1]) }

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
                    icon = null
                )
            )
        },
        isConfirmEnabled = title.isNotBlank(),
        onDismiss = onDismiss
    ) {
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
                label = { Text(text = stringResource(uiR.string.title)) },
                placeholder = { Text(text = stringResource(uiR.string.title) + "*") },
                supportingText = { Text(text = stringResource(uiR.string.required)) },
                maxLines = 1,
                modifier = Modifier.focusRequester(titleTextField)
            )
            IconPickerDropdownMenuBox(
                iconName = icon,
                onIconClick = {
                    icon = it.name.split(".")[1]
                }
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
                    icon = null
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
                label = { Text(text = stringResource(uiR.string.title)) },
                placeholder = { Text(text = stringResource(uiR.string.title) + "*") },
                supportingText = { Text(text = stringResource(uiR.string.required)) },
                maxLines = 1
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IconPickerDropdownMenuBox(
    iconName: String,
    onIconClick: (ImageVector) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val icons = listOf(
        Icons.Outlined.ShoppingCart,
        Icons.Outlined.Restaurant,
        Icons.Outlined.Fastfood,
        Icons.Outlined.DirectionsBus,
        Icons.Outlined.Medication,
        Icons.Outlined.Checkroom,
        Icons.Outlined.Memory,
        Icons.Outlined.SportsEsports,
        Icons.Outlined.Map,
        Icons.Outlined.FitnessCenter,
        Icons.Outlined.Brush
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = iconName,
            onValueChange = {},
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
            label = { Text(text = "Icon") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            leadingIcon = {
                Icon(imageVector = getIconByName(iconName), contentDescription = null)
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            icons.forEach { icon ->
                DropdownMenuItem(
                    text = { Text(icon.name.split(".")[1]) },
                    leadingIcon = {
                        Icon(
                            imageVector = getIconByName(icon.name.split(".")[1]),
                            contentDescription = null
                        )
                    },
                    onClick = {
                        onIconClick(icon)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

private fun getIconByName(name: String): ImageVector {
    val cl = Class.forName("androidx.compose.material.icons.outlined.${name}Kt")
    val method = cl.declaredMethods.first()
    return method.invoke(null, Icons.Outlined) as ImageVector
}