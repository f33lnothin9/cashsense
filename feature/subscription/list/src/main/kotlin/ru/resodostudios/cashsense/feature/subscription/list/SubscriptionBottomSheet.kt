package ru.resodostudios.cashsense.feature.subscription.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.component.CsModalBottomSheet
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.Subscription
import ru.resodostudios.cashsense.core.ui.FormatDateType.DATE
import ru.resodostudios.cashsense.core.ui.formatAmount
import ru.resodostudios.cashsense.core.ui.formatDate
import ru.resodostudios.cashsense.feature.subscription.dialog.RepeatingIntervalType.DAILY
import ru.resodostudios.cashsense.feature.subscription.dialog.RepeatingIntervalType.MONTHLY
import ru.resodostudios.cashsense.feature.subscription.dialog.RepeatingIntervalType.WEEKLY
import ru.resodostudios.cashsense.feature.subscription.dialog.RepeatingIntervalType.YEARLY
import ru.resodostudios.cashsense.feature.subscription.dialog.getRepeatingIntervalType
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SubscriptionBottomSheet(
    subscription: Subscription,
    onDismiss: () -> Unit,
    onEdit: (String) -> Unit,
    onDelete: () -> Unit,
) {
    CsModalBottomSheet(onDismiss) {
        Column {
            CsListItem(
                headlineContent = {
                    Text(
                        text = subscription.title,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = ImageVector.vectorResource(CsIcons.AutoRenew),
                        contentDescription = null,
                    )
                },
                supportingContent = {
                    Text(
                        text = subscription.amount.formatAmount(subscription.currency),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                }
            )
            FlowRow(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CsTag(
                    text = subscription.paymentDate.formatDate(DATE),
                    iconId = CsIcons.Calendar,
                )
                AnimatedVisibility(subscription.reminder != null) {
                    val repeatingIntervalType = getRepeatingIntervalType(subscription.reminder?.repeatingInterval)
                    val reminderTitle = when (repeatingIntervalType) {
                        DAILY -> stringResource(localesR.string.repeat_daily)
                        WEEKLY -> stringResource(localesR.string.repeat_weekly)
                        MONTHLY -> stringResource(localesR.string.repeat_monthly)
                        YEARLY -> stringResource(localesR.string.repeat_yearly)
                        else -> stringResource(localesR.string.reminder)
                    }
                    CsTag(
                        text = reminderTitle,
                        iconId = CsIcons.NotificationsActive,
                    )
                }
            }
            HorizontalDivider(Modifier.padding(16.dp))
            CsListItem(
                headlineContent = { Text(stringResource(localesR.string.edit)) },
                leadingContent = {
                    Icon(
                        imageVector = ImageVector.vectorResource(CsIcons.Edit),
                        contentDescription = null,
                    )
                },
                onClick = {
                    onDismiss()
                    onEdit(subscription.id)
                },
            )
            CsListItem(
                headlineContent = { Text(stringResource(localesR.string.delete)) },
                leadingContent = {
                    Icon(
                        imageVector = ImageVector.vectorResource(CsIcons.Delete),
                        contentDescription = null,
                    )
                },
                onClick = {
                    onDismiss()
                    onDelete()
                },
            )
        }
    }
}