package ru.resodostudios.cashsense.feature.subscriptions

import androidx.compose.runtime.Composable
import ru.resodostudios.cashsense.core.ui.EmptyState

@Composable
internal fun SubscriptionsRoute(

) {
    SubscriptionsScreen()
}

@Composable
internal fun SubscriptionsScreen(

) {
    EmptyState(
        messageId = R.string.feature_subscriptions_empty_message,
        animationId = R.raw.anim_empty_state
    )
}