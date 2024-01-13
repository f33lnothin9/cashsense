package ru.resodostudios.cashsense.feature.subscriptions

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import ru.resodostudios.cashsense.core.model.data.Subscription

@Composable
internal fun EditSubscriptionRoute(
    onBackClick: () -> Unit,
    viewModel: SubscriptionsViewModel = hiltViewModel()
) {
    EditSubscriptionScreen(
        onBackClick = onBackClick,
        onConfirmClick = viewModel::upsertSubscription
    )
}

@Composable
internal fun EditSubscriptionScreen(
    onBackClick: () -> Unit,
    onConfirmClick: (Subscription) -> Unit
) {

}