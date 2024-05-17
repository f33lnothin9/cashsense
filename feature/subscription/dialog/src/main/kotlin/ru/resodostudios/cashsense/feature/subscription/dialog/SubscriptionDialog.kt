package ru.resodostudios.cashsense.feature.subscription.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.Instant
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.ui.CurrencyExposedDropdownMenuBox
import ru.resodostudios.cashsense.core.ui.DatePickerTextField
import ru.resodostudios.cashsense.core.ui.formatDate
import ru.resodostudios.cashsense.core.ui.validateAmount
import ru.resodostudios.cashsense.core.ui.R as uiR

@Composable
fun SubscriptionDialog(
    onDismiss: () -> Unit,
    viewModel: SubscriptionDialogViewModel = hiltViewModel(),
) {
    val subscriptionDialogState by viewModel.subscriptionDialogUiState.collectAsStateWithLifecycle()

    SubscriptionDialog(
        subscriptionDialogState = subscriptionDialogState,
        onSubscriptionEvent = viewModel::onSubscriptionEvent,
        onDismiss = onDismiss,
    )
}

@Composable
fun SubscriptionDialog(
    subscriptionDialogState: SubscriptionDialogUiState,
    onSubscriptionEvent: (SubscriptionDialogEvent) -> Unit,
    onDismiss: () -> Unit,
) {
    val dialogTitle = if (subscriptionDialogState.id.isNotEmpty()) R.string.feature_subscription_dialog_edit else R.string.feature_subscription_dialog_new
    val dialogConfirmText = if (subscriptionDialogState.id.isNotEmpty()) uiR.string.save else uiR.string.add

    CsAlertDialog(
        titleRes = dialogTitle,
        confirmButtonTextRes = dialogConfirmText,
        dismissButtonTextRes = uiR.string.core_ui_cancel,
        iconRes = CsIcons.Subscriptions,
        onConfirm = {
            onSubscriptionEvent(SubscriptionDialogEvent.Save)
            onDismiss()
        },
        isConfirmEnabled = subscriptionDialogState.title.isNotBlank() &&
                subscriptionDialogState.amount.validateAmount().second,
        onDismiss = onDismiss,
    ) {
        val (titleTextField, amountTextField) = remember { FocusRequester.createRefs() }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) {
            OutlinedTextField(
                value = subscriptionDialogState.title,
                onValueChange = { onSubscriptionEvent(SubscriptionDialogEvent.UpdateTitle(it)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                label = { Text(stringResource(uiR.string.title)) },
                placeholder = { Text(stringResource(uiR.string.title) + "*") },
                supportingText = { Text(stringResource(uiR.string.required)) },
                maxLines = 1,
                modifier = Modifier
                    .focusRequester(titleTextField)
                    .focusProperties { next = amountTextField }
                    .fillMaxWidth(),
            )
            OutlinedTextField(
                value = subscriptionDialogState.amount,
                onValueChange = { onSubscriptionEvent(SubscriptionDialogEvent.UpdateAmount(it.validateAmount().first)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done,
                ),
                label = { Text(stringResource(uiR.string.amount)) },
                placeholder = { Text(stringResource(uiR.string.amount) + "*") },
                supportingText = { Text(stringResource(uiR.string.required)) },
                maxLines = 1,
                modifier = Modifier
                    .focusRequester(amountTextField)
                    .fillMaxWidth(),
            )
            DatePickerTextField(
                value = subscriptionDialogState.paymentDate.formatDate(),
                labelTextId = R.string.feature_subscription_dialog_payment_date,
                iconId = CsIcons.Calendar,
                onDateClick = { onSubscriptionEvent(SubscriptionDialogEvent.UpdatePaymentDate(Instant.fromEpochMilliseconds(it))) },
                modifier = Modifier.fillMaxWidth(),
                initialSelectedDateMillis = subscriptionDialogState.paymentDate.toEpochMilliseconds(),
                isAllDatesEnabled = false,
            )
            CurrencyExposedDropdownMenuBox(
                currencyName = subscriptionDialogState.currency,
                onCurrencyClick = { onSubscriptionEvent(SubscriptionDialogEvent.UpdateCurrency(it.name)) },
                modifier = Modifier.fillMaxWidth(),
            )
            CsListItem(
                headlineContent = { Text(stringResource(R.string.feature_subscription_dialog_reminder)) },
                supportingContent = { Text(stringResource(R.string.feature_subscription_dialog_reminder_description)) },
                leadingContent = {
                    Icon(
                        imageVector = ImageVector.vectorResource(CsIcons.Notifications),
                        contentDescription = null,
                    )
                },
                trailingContent = {
                    Switch(
                        checked = subscriptionDialogState.isReminderEnabled,
                        onCheckedChange = { onSubscriptionEvent(SubscriptionDialogEvent.UpdateReminderSwitch(it)) },
                    )
                }
            )
        }
        LaunchedEffect(Unit) {
            if (subscriptionDialogState.id.isEmpty()) {
                titleTextField.requestFocus()
            }
        }
    }
}