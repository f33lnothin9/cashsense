package ru.resodostudios.cashsense.feature.subscription.list

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import kotlinx.datetime.Instant
import ru.resodostudios.cashsense.core.model.data.Reminder
import ru.resodostudios.cashsense.core.model.data.Subscription
import ru.resodostudios.cashsense.feature.subscription.dialog.RepeatingIntervalType
import java.math.BigDecimal

/**
 * This [PreviewParameterProvider](https://developer.android.com/reference/kotlin/androidx/compose/ui/tooling/preview/PreviewParameterProvider)
 * provides list of [Subscription] for Composable previews.
 */
internal class SubscriptionPreviewParameterProvider : PreviewParameterProvider<List<Subscription>> {

    override val values: Sequence<List<Subscription>>
        get() = sequenceOf(
            listOf(
                Subscription(
                    id = "0",
                    title = "Google One",
                    amount = BigDecimal(9.99),
                    currency = "USD",
                    paymentDate = Instant.DISTANT_PAST,
                    reminder = Reminder(
                        id = 0,
                        notificationDate = Instant.DISTANT_PAST,
                        repeatingInterval = RepeatingIntervalType.MONTHLY.period,
                    ),
                ),
                Subscription(
                    id = "0",
                    title = "Apple Music",
                    amount = BigDecimal(39.99),
                    currency = "TRY",
                    paymentDate = Instant.DISTANT_PAST,
                    reminder = null,
                ),
                Subscription(
                    id = "0",
                    title = "Spotify Premium",
                    amount = BigDecimal(399.99),
                    currency = "TRY",
                    paymentDate = Instant.DISTANT_PAST,
                    reminder = Reminder(
                        id = 0,
                        notificationDate = Instant.DISTANT_PAST,
                        repeatingInterval = RepeatingIntervalType.YEARLY.period,
                    ),
                ),
                Subscription(
                    id = "0",
                    title = "Yandex Plus",
                    amount = BigDecimal(99),
                    currency = "RUB",
                    paymentDate = Instant.DISTANT_PAST,
                    reminder = Reminder(
                        id = 0,
                        notificationDate = Instant.DISTANT_PAST,
                        repeatingInterval = RepeatingIntervalType.MONTHLY.period,
                    ),
                ),
                Subscription(
                    id = "0",
                    title = "ChatGPT Plus",
                    amount = BigDecimal(19.99),
                    currency = "USD",
                    paymentDate = Instant.DISTANT_PAST,
                    reminder = null,
                ),
            ),
        )
}