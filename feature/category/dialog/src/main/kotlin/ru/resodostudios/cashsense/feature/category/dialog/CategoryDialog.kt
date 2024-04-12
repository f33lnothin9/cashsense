package ru.resodostudios.cashsense.feature.category.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.ui.IconPickerDropdownMenu
import ru.resodostudios.cashsense.core.ui.R as uiR

@Composable
fun CategoryDialog(
    onDismiss: () -> Unit,
    viewModel: CategoryDialogViewModel = hiltViewModel(),
) {
    val categoryDialogState by viewModel.categoryDialogUiState.collectAsStateWithLifecycle()

    CategoryDialog(
        categoryDialogState = categoryDialogState,
        onCategoryEvent = viewModel::onCategoryEvent,
        onDismiss = onDismiss,
    )
}

@Composable
fun CategoryDialog(
    categoryDialogState: CategoryDialogUiState,
    onCategoryEvent: (CategoryDialogEvent) -> Unit,
    onDismiss: () -> Unit,
) {
    val dialogTitle = if (categoryDialogState.id.isNotEmpty()) R.string.feature_category_dialog_edit_category else R.string.feature_category_dialog_new_category
    val dialogConfirmText = if (categoryDialogState.id.isNotEmpty()) uiR.string.save else uiR.string.add

    CsAlertDialog(
        titleRes = dialogTitle,
        confirmButtonTextRes = dialogConfirmText,
        dismissButtonTextRes = uiR.string.core_ui_cancel,
        iconRes = CsIcons.Category,
        onConfirm = {
            onCategoryEvent(CategoryDialogEvent.Save)
            onDismiss()
        },
        isConfirmEnabled = categoryDialogState.title.isNotBlank(),
        onDismiss = onDismiss,
    ) {
        val focusManager = LocalFocusManager.current
        val titleTextField = remember { FocusRequester() }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) {
            OutlinedTextField(
                value = categoryDialogState.title,
                onValueChange = { onCategoryEvent(CategoryDialogEvent.UpdateTitle(it)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                label = { Text(stringResource(uiR.string.core_ui_icon_and_title)) },
                placeholder = { Text(stringResource(uiR.string.title) + "*") },
                supportingText = { Text(stringResource(uiR.string.required)) },
                maxLines = 1,
                leadingIcon = {
                    IconPickerDropdownMenu(
                        currentIconId = categoryDialogState.icon,
                        onSelectedIconClick = { onCategoryEvent(CategoryDialogEvent.UpdateIcon(it)) },
                        onClick = { focusManager.clearFocus() },
                    )
                },
                modifier = Modifier.focusRequester(titleTextField),
            )
        }
        LaunchedEffect(Unit) {
            if (categoryDialogState.id.isEmpty()) {
                titleTextField.requestFocus()
            }
        }
    }
}