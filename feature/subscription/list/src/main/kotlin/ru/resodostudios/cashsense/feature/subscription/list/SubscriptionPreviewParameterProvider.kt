package ru.resodostudios.cashsense.feature.subscription.list

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import kotlinx.datetime.Instant
import ru.resodostudios.cashsense.core.model.data.Reminder
import ru.resodostudios.cashsense.core.model.data.RepeatingIntervalType.MONTHLY
import ru.resodostudios.cashsense.core.model.data.RepeatingIntervalType.YEARLY
import ru.resodostudios.cashsense.core.model.data.Subscription
import java.math.BigDecimal
import java.util.Currency

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
                    currency = Currency.getInstance("USD"),
                    paymentDate = Instant.DISTANT_PAST,
                    reminder = Reminder(
                        id = 0,
                        notificationDate = Instant.DISTANT_PAST,
                        repeatingInterval = MONTHLY.period,
                    ),
                ),
                Subscription(
                    id = "0",
                    title = "Apple Music",
                    amount = BigDecimal(39.99),
                    currency = Currency.getInstance("TRY"),
                    paymentDate = Instant.DISTANT_PAST,
                    reminder = null,
                ),
                Subscription(
                    id = "0",
                    title = "Spotify Premium",
                    amount = BigDecimal(399.99),
                    currency = Currency.getInstance("TRY"),
                    paymentDate = Instant.DISTANT_PAST,
                    reminder = Reminder(
                        id = 0,
                        notificationDate = Instant.DISTANT_PAST,
                        repeatingInterval = YEARLY.period,
                    ),
                ),
                Subscription(
                    id = "0",
                    title = "Yandex Plus",
                    amount = BigDecimal(99),
                    currency = Currency.getInstance("RUB"),
                    paymentDate = Instant.DISTANT_PAST,
                    reminder = Reminder(
                        id = 0,
                        notificationDate = Instant.DISTANT_PAST,
                        repeatingInterval = MONTHLY.period,
                    ),
                ),
                Subscription(
                    id = "0",
                    title = "ChatGPT Plus",
                    amount = BigDecimal(19.99),
                    currency = Currency.getInstance("USD"),
                    paymentDate = Instant.DISTANT_PAST,
                    reminder = null,
                ),
            ),
        )
}