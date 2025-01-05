package ru.resodostudios.cashsense.feature.subscription.dialog

import android.Manifest
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
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
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.Reminder
import ru.resodostudios.cashsense.core.model.data.RepeatingIntervalType
import ru.resodostudios.cashsense.core.model.data.Subscription
import ru.resodostudios.cashsense.core.ui.CurrencyDropdownMenu
import ru.resodostudios.cashsense.core.ui.DatePickerTextField
import ru.resodostudios.cashsense.core.ui.util.cleanAmount
import ru.resodostudios.cashsense.core.ui.util.formatDate
import ru.resodostudios.cashsense.core.ui.util.isAmountValid
import ru.resodostudios.cashsense.feature.subscription.dialog.SubscriptionDialogEvent.Save
import ru.resodostudios.cashsense.feature.subscription.dialog.SubscriptionDialogEvent.UpdateAmount
import ru.resodostudios.cashsense.feature.subscription.dialog.SubscriptionDialogEvent.UpdateCurrency
import ru.resodostudios.cashsense.feature.subscription.dialog.SubscriptionDialogEvent.UpdatePaymentDate
import ru.resodostudios.cashsense.feature.subscription.dialog.SubscriptionDialogEvent.UpdateReminderSwitch
import ru.resodostudios.cashsense.feature.subscription.dialog.SubscriptionDialogEvent.UpdateRepeatingInterval
import ru.resodostudios.cashsense.feature.subscription.dialog.SubscriptionDialogEvent.UpdateTitle
import kotlin.uuid.Uuid
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
    val (dialogTitle, dialogConfirmText) = if (subscriptionDialogState.id.isNotEmpty()) {
        localesR.string.edit_subscription to localesR.string.save
    } else {
        localesR.string.new_subscription to localesR.string.add
    }

    CsAlertDialog(
        titleRes = dialogTitle,
        confirmButtonTextRes = dialogConfirmText,
        dismissButtonTextRes = localesR.string.cancel,
        iconRes = CsIcons.AutoRenew,
        onConfirm = {
            val subscriptionId = subscriptionDialogState.id.ifBlank { Uuid.random().toHexString() }
            var reminder: Reminder? = null

            if (subscriptionDialogState.isReminderEnabled) {
                val timeZone = TimeZone.currentSystemDefault()
                val currentInstant = subscriptionDialogState.paymentDate
                val currentDateTime = currentInstant.toLocalDateTime(timeZone)
                val previousDate = currentDateTime.date.minus(1, DateTimeUnit.DAY)
                val notificationDate = LocalDateTime(previousDate, LocalTime(9, 0))
                    .toInstant(timeZone)
                reminder = Reminder(
                    id = subscriptionId.hashCode(),
                    notificationDate = notificationDate,
                    repeatingInterval = subscriptionDialogState.repeatingInterval.period,
                )
            }

            val subscription = Subscription(
                id = subscriptionId,
                title = subscriptionDialogState.title,
                amount = subscriptionDialogState.amount.toBigDecimal(),
                paymentDate = subscriptionDialogState.paymentDate,
                currency = subscriptionDialogState.currency,
                reminder = reminder,
            )
            onSubscriptionEvent(Save(subscription))
            onDismiss()
        },
        isConfirmEnabled = subscriptionDialogState.title.isNotBlank() &&
                subscriptionDialogState.amount.isAmountValid(),
        onDismiss = onDismiss,
    ) {
        val focusManager = LocalFocusManager.current
        val focusRequester = remember { FocusRequester() }

        Column(Modifier.verticalScroll(rememberScrollState())) {
            OutlinedTextField(
                value = subscriptionDialogState.title,
                onValueChange = { onSubscriptionEvent(UpdateTitle(it)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                ),
                singleLine = true,
                label = { Text(stringResource(localesR.string.title)) },
                placeholder = { Text(stringResource(localesR.string.title) + "*") },
                supportingText = { Text(stringResource(localesR.string.required)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .focusRequester(focusRequester),
            )
            OutlinedTextField(
                value = subscriptionDialogState.amount,
                onValueChange = { onSubscriptionEvent(UpdateAmount(it.cleanAmount())) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() },
                ),
                singleLine = true,
                label = { Text(stringResource(localesR.string.amount)) },
                placeholder = { Text(stringResource(localesR.string.amount) + "*") },
                supportingText = { Text(stringResource(localesR.string.required)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            )
            CurrencyDropdownMenu(
                currency = subscriptionDialogState.currency,
                onCurrencyClick = { onSubscriptionEvent(UpdateCurrency(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
            )
            DatePickerTextField(
                value = subscriptionDialogState.paymentDate.formatDate(),
                labelTextId = localesR.string.payment_date,
                iconId = CsIcons.Calendar,
                onDateClick = {
                    onSubscriptionEvent(
                        UpdatePaymentDate(
                            Instant.fromEpochMilliseconds(
                                it
                            )
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                initialSelectedDateMillis = subscriptionDialogState.paymentDate.toEpochMilliseconds(),
                isAllDatesEnabled = false,
            )
            CsListItem(
                headlineContent = { Text(stringResource(localesR.string.reminder)) },
                supportingContent = { Text(stringResource(localesR.string.reminder_description)) },
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
                focusRequester.requestFocus()
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
        stringResource(localesR.string.repeat_none),
        stringResource(localesR.string.repeat_daily),
        stringResource(localesR.string.repeat_weekly),
        stringResource(localesR.string.repeat_monthly),
        stringResource(localesR.string.repeat_yearly),
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
            label = { Text(stringResource(localesR.string.repeating_interval)) },
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