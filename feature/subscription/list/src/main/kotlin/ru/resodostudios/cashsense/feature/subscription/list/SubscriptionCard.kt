package ru.resodostudios.cashsense.feature.subscription.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import ru.resodostudios.cashsense.core.designsystem.component.CsTag
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.Subscription
import ru.resodostudios.cashsense.core.ui.FormatDateType.DATE
import ru.resodostudios.cashsense.core.ui.formatAmount
import ru.resodostudios.cashsense.core.ui.formatDate
import ru.resodostudios.cashsense.feature.subscription.dialog.R
import ru.resodostudios.cashsense.feature.subscription.dialog.RepeatingIntervalType.DAILY
import ru.resodostudios.cashsense.feature.subscription.dialog.RepeatingIntervalType.MONTHLY
import ru.resodostudios.cashsense.feature.subscription.dialog.RepeatingIntervalType.WEEKLY
import ru.resodostudios.cashsense.feature.subscription.dialog.RepeatingIntervalType.YEARLY
import ru.resodostudios.cashsense.feature.subscription.dialog.getRepeatingIntervalType
import java.math.BigDecimal

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubscriptionCard(
    subscription: Subscription,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        onClick = { onClick(subscription.id) },
        shape = RoundedCornerShape(24.dp),
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = subscription.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = subscription.amount
                    .formatAmount(subscription.currency),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyLarge,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 12.dp),
            ) {
                CsTag(
                    text = subscription.paymentDate.formatDate(DATE),
                    iconId = CsIcons.Calendar,
                )
                AnimatedVisibility(
                    visible = subscription.reminder != null,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                ) {
                    val repeatingIntervalType = subscription.reminder?.repeatingInterval?.let {
                        getRepeatingIntervalType(it)
                    }
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
        }
    }
}

@PreviewLightDark
@Composable
fun SubscriptionCardPreview() {
    CsTheme {
        Surface {
            SubscriptionCard(
                subscription = Subscription(
                    id = "",
                    title = "Spotify Premium",
                    amount = BigDecimal(14.99),
                    currency = "USD",
                    paymentDate = Clock.System.now(),
                    reminder = null,
                ),
                onClick = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}