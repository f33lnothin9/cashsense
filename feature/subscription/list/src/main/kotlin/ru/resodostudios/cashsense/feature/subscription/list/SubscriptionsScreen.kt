package ru.resodostudios.cashsense.feature.subscription.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.formatAmount
import ru.resodostudios.cashsense.core.ui.formatDate

@Composable
internal fun SubscriptionsRoute(
    viewModel: SubscriptionsViewModel = hiltViewModel(),
) {
    val subscriptionsState by viewModel.subscriptionsUiState.collectAsStateWithLifecycle()

    SubscriptionsScreen(
        subscriptionsState = subscriptionsState,
    )
}

@Composable
internal fun SubscriptionsScreen(
    subscriptionsState: SubscriptionsUiState,
) {
    when (subscriptionsState) {
        SubscriptionsUiState.Loading -> LoadingState()
        is SubscriptionsUiState.Success -> {
            if (subscriptionsState.subscriptions.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(300.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(subscriptionsState.subscriptions) { subscription ->
                        ListItem(
                            headlineContent = { Text(subscription.title) },
                            supportingContent = { Text(subscription.amount.formatAmount(subscription.currency)) },
                            overlineContent = { Text(subscription.paymentDate.formatDate()) },
                        )
                    }
                }
            } else {
                EmptyState(
                    messageRes = R.string.feature_subscription_list_empty_message,
                    animationRes = R.raw.anim_subscriptions_empty,
                )
            }
        }
    }
}