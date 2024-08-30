package ru.resodostudios.cashsense.feature.subscription.dialog

import android.Manifest
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus.Denied
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.datetime.Instant
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.ui.CurrencyDropdownMenu
import ru.resodostudios.cashsense.core.ui.DatePickerTextField
import ru.resodostudios.cashsense.core.ui.formatDate
import ru.resodostudios.cashsense.core.ui.validateAmount
import ru.resodostudios.cashsense.feature.subscription.dialog.SubscriptionDialogEvent.Save
import ru.resodostudios.cashsense.feature.subscription.dialog.SubscriptionDialogEvent.UpdateAmount
import ru.resodostudios.cashsense.feature.subscription.dialog.SubscriptionDialogEvent.UpdateCurrency
import ru.resodostudios.cashsense.feature.subscription.dialog.SubscriptionDialogEvent.UpdatePaymentDate
import ru.resodostudios.cashsense.feature.subscription.dialog.SubscriptionDialogEvent.UpdateReminderSwitch
import ru.resodostudios.cashsense.feature.subscription.dialog.SubscriptionDialogEvent.UpdateRepeatingInterval
import ru.resodostudios.cashsense.feature.subscription.dialog.SubscriptionDialogEvent.UpdateTitle
import ru.resodostudios.cashsense.core.locales.R as localesR

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
    val dialogTitle =
        if (subscriptionDialogState.id.isNotEmpty()) R.string.feature_subscription_dialog_edit else R.string.feature_subscription_dialog_new
    val dialogConfirmText =
        if (subscriptionDialogState.id.isNotEmpty()) localesR.string.save else localesR.string.add

    CsAlertDialog(
        titleRes = dialogTitle,
        confirmButtonTextRes = dialogConfirmText,
        dismissButtonTextRes = localesR.string.cancel,
        iconRes = CsIcons.AutoRenew,
        onConfirm = {
            onSubscriptionEvent(Save)
            onDismiss()
        },
        isConfirmEnabled = subscriptionDialogState.title.isNotBlank() &&
                subscriptionDialogState.amount.validateAmount().second,
        onDismiss = onDismiss,
    ) {
        val (titleTextField, amountTextField) = remember { FocusRequester.createRefs() }

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) {
            OutlinedTextField(
                value = subscriptionDialogState.title,
                onValueChange = { onSubscriptionEvent(UpdateTitle(it)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                label = { Text(stringResource(localesR.string.title)) },
                placeholder = { Text(stringResource(localesR.string.title) + "*") },
                supportingText = { Text(stringResource(localesR.string.required)) },
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .focusRequester(titleTextField)
                    .focusProperties { next = amountTextField },
            )
            OutlinedTextField(
                value = subscriptionDialogState.amount,
                onValueChange = { onSubscriptionEvent(UpdateAmount(it.validateAmount().first)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done,
                ),
                label = { Text(stringResource(localesR.string.amount)) },
                placeholder = { Text(stringResource(localesR.string.amount) + "*") },
                supportingText = { Text(stringResource(localesR.string.required)) },
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .focusRequester(amountTextField),
            )
            DatePickerTextField(
                value = subscriptionDialogState.paymentDate.formatDate(),
                labelTextId = R.string.feature_subscription_dialog_payment_date,
                iconId = CsIcons.Calendar,
                onDateClick = { onSubscriptionEvent(UpdatePaymentDate(Instant.fromEpochMilliseconds(it))) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                initialSelectedDateMillis = subscriptionDialogState.paymentDate.toEpochMilliseconds(),
                isAllDatesEnabled = false,
            )
            CurrencyDropdownMenu(
                currencyName = subscriptionDialogState.currency,
                onCurrencyClick = { onSubscriptionEvent(UpdateCurrency(it.currencyCode)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
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
                        onCheckedChange = { onSubscriptionEvent(UpdateReminderSwitch(it)) },
                    )
                },
            )
            AnimatedVisibility(subscriptionDialogState.isReminderEnabled) {
                RepeatingIntervalDropdownMenu(
                    interval = subscriptionDialogState.repeatingInterval,
                    onIntervalChange = { onSubscriptionEvent(UpdateRepeatingInterval(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )
            }
        }
        LaunchedEffect(Unit) {
            if (subscriptionDialogState.id.isEmpty()) {
                titleTextField.requestFocus()
            }
        }

        if (subscriptionDialogState.isReminderEnabled) NotificationPermissionEffect()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepeatingIntervalDropdownMenu(
    interval: RepeatingIntervalType,
    onIntervalChange: (RepeatingIntervalType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val intervalNames = listOf(
        stringResource(R.string.feature_subscription_dialog_repeat_none),
        stringResource(R.string.feature_subscription_dialog_repeat_daily),
        stringResource(R.string.feature_subscription_dialog_repeat_weekly),
        stringResource(R.string.feature_subscription_dialog_repeat_monthly),
        stringResource(R.string.feature_subscription_dialog_repeat_yearly),
    )
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = intervalNames[interval.ordinal],
            onValueChange = {},
            modifier = modifier.menuAnchor(MenuAnchorType.PrimaryEditable),
            readOnly = true,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
            ),
            label = { Text(stringResource(R.string.feature_subscription_dialog_repeating_interval)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            intervalNames.forEachIndexed { index, label ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onIntervalChange(RepeatingIntervalType.entries[index])
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun NotificationPermissionEffect() {

    if (LocalInspectionMode.current) return
    if (VERSION.SDK_INT < VERSION_CODES.TIRAMISU) return

    val notificationsPermissionState = rememberPermissionState(
        Manifest.permission.POST_NOTIFICATIONS,
    )

    LaunchedEffect(notificationsPermissionState) {
        val status = notificationsPermissionState.status
        if (status is Denied && !status.shouldShowRationale) {
            notificationsPermissionState.launchPermissionRequest()
        }
    }
}