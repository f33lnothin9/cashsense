package ru.resodostudios.cashsense.feature.subscription.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.component.CsModalBottomSheet
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.ui.FormatDateType.DATE
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.formatDate
import ru.resodostudios.cashsense.feature.subscription.dialog.RepeatingIntervalType.DAILY
import ru.resodostudios.cashsense.feature.subscription.dialog.RepeatingIntervalType.MONTHLY
import ru.resodostudios.cashsense.feature.subscription.dialog.RepeatingIntervalType.WEEKLY
import ru.resodostudios.cashsense.feature.subscription.dialog.RepeatingIntervalType.YEARLY
import ru.resodostudios.cashsense.core.ui.R as uiR

@Composable
fun SubscriptionBottomSheet(
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: (String) -> Unit,
    viewModel: SubscriptionDialogViewModel = hiltViewModel(),
) {
    val subscriptionDialogState by viewModel.subscriptionDialogUiState.collectAsStateWithLifecycle()

    SubscriptionBottomSheet(
        subscriptionDialogState = subscriptionDialogState,
        onDismiss = onDismiss,
        onEdit = onEdit,
        onDelete = onDelete,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubscriptionBottomSheet(
    subscriptionDialogState: SubscriptionDialogUiState,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: (String) -> Unit,
) {
    CsModalBottomSheet(onDismiss) {
        if (subscriptionDialogState.isLoading) {
            LoadingState(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
            )
        }
        if (!subscriptionDialogState.isLoading) {
            Column {
                CsListItem(
                    headlineContent = { Text(subscriptionDialogState.title) },
                    leadingContent = {
                        Icon(
                            imageVector = ImageVector.vectorResource(CsIcons.Subscriptions),
                            contentDescription = null,
                        )
                    },
                )
                FlowRow(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CsTag(
                        text = subscriptionDialogState.paymentDate.formatDate(DATE),
                        iconId = CsIcons.Calendar,
                    )
                    AnimatedVisibility(subscriptionDialogState.isReminderEnabled) {
                        val repeatingIntervalType =
                            getRepeatingIntervalType(subscriptionDialogState.repeatingInterval.period)
                        val reminderTitle = when (repeatingIntervalType) {
                            DAILY -> stringResource(R.string.feature_subscription_dialog_repeat_daily)
                            WEEKLY -> stringResource(R.string.feature_subscription_dialog_repeat_weekly)
                            MONTHLY -> stringResource(R.string.feature_subscription_dialog_repeat_monthly)
                            YEARLY -> stringResource(R.string.feature_subscription_dialog_repeat_yearly)
                            else -> stringResource(R.string.feature_subscription_dialog_reminder)
                        }
                        CsTag(
                            text = reminderTitle,
                            iconId = CsIcons.NotificationsActive,
                        )
                    }
                }
                HorizontalDivider(Modifier.padding(16.dp))
                CsListItem(
                    headlineContent = { Text(stringResource(uiR.string.edit)) },
                    leadingContent = {
                        Icon(
                            imageVector = ImageVector.vectorResource(CsIcons.Edit),
                            contentDescription = null,
                        )
                    },
                    onClick = {
                        onDismiss()
                        onEdit()
                    },
                )
                CsListItem(
                    headlineContent = { Text(stringResource(uiR.string.delete)) },
                    leadingContent = {
                        Icon(
                            imageVector = ImageVector.vectorResource(CsIcons.Delete),
                            contentDescription = null,
                        )
                    },
                    onClick = {
                        onDismiss()
                        onDelete(subscriptionDialogState.id)
                    },
                )
            }
        }
    }
}