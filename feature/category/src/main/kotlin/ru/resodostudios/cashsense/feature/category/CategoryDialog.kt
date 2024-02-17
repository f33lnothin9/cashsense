package ru.resodostudios.cashsense.feature.category

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.ui.getIconId
import ru.resodostudios.cashsense.core.ui.getIconName
import ru.resodostudios.cashsense.core.ui.R as uiR

@Composable
fun CategoryDialog(
    onDismiss: () -> Unit,
    viewModel: CategoryViewModel = hiltViewModel(),
) {
    val categoryState by viewModel.categoryUiState.collectAsStateWithLifecycle()

    CategoryDialog(
        categoryState = categoryState,
        onCategoryEvent = viewModel::onCategoryEvent,
        onDismiss = onDismiss,
    )
}

@Composable
fun CategoryDialog(
    categoryState: CategoryUiState,
    onCategoryEvent: (CategoryEvent) -> Unit,
    onDismiss: () -> Unit,
) {
    val dialogTitle = if (categoryState.isEditing) R.string.feature_category_edit_category else R.string.feature_category_new_category
    val dialogConfirmText = if (categoryState.isEditing) uiR.string.save else uiR.string.add

    CsAlertDialog(
        titleRes = dialogTitle,
        confirmButtonTextRes = dialogConfirmText,
        dismissButtonTextRes = uiR.string.core_ui_cancel,
        iconRes = CsIcons.Category,
        onConfirm = {
            onCategoryEvent(CategoryEvent.Confirm)
            onDismiss()
        },
        isConfirmEnabled = categoryState.title.text.isNotBlank(),
        onDismiss = onDismiss,
    ) {
        val titleTextField = remember { FocusRequester() }

        val context = LocalContext.current

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = categoryState.title,
                onValueChange = { onCategoryEvent(CategoryEvent.UpdateTitle(it)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                label = { Text(text = stringResource(uiR.string.core_ui_icon_and_title)) },
                placeholder = { Text(text = stringResource(uiR.string.title) + "*") },
                supportingText = { Text(text = stringResource(uiR.string.required)) },
                maxLines = 1,
                leadingIcon = {
                    IconPickerDropdownMenu(
                        context = context,
                        currentIcon = categoryState.icon.ifBlank { CsIcons.Category.getIconName(context) },
                        onIconClick = { onCategoryEvent(CategoryEvent.UpdateIcon(it)) }
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IconPickerDropdownMenu(
    context: Context,
    currentIcon: String,
    onIconClick: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    val icons = listOf(
        CsIcons.Category,
        CsIcons.AccountBalance,
        CsIcons.Apparel,
        CsIcons.Chair,
        CsIcons.Exercise,
        CsIcons.Fastfood,
        CsIcons.DirectionsBus,
        CsIcons.Handyman,
        CsIcons.Language,
        CsIcons.LocalBar,
        CsIcons.LocalGasStation,
        CsIcons.Memory,
        CsIcons.Payments,
        CsIcons.Pets,
        CsIcons.Phishing,
        CsIcons.Pill,
        CsIcons.Transaction,
        CsIcons.Restaurant,
        CsIcons.School,
        CsIcons.SelfCare,
        CsIcons.ShoppingCart,
        CsIcons.SimCard,
        CsIcons.SmokingRooms,
        CsIcons.SportsEsports,
        CsIcons.Travel
    )

    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = ImageVector.vectorResource(currentIcon.getIconId(context)),
                contentDescription = null
            )
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
                            onIconClick(icon.getIconName(context))
                            expanded = false
                        }
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(icon),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}