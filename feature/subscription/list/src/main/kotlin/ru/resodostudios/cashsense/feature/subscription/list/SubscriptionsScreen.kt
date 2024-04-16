package ru.resodostudios.cashsense.feature.subscription.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.formatAmount
import ru.resodostudios.cashsense.core.ui.formatDate
import ru.resodostudios.cashsense.feature.subscription.dialog.SubscriptionBottomSheet
import ru.resodostudios.cashsense.feature.subscription.dialog.SubscriptionDialog
import ru.resodostudios.cashsense.feature.subscription.dialog.SubscriptionDialogEvent
import ru.resodostudios.cashsense.feature.subscription.dialog.SubscriptionDialogViewModel

@Composable
internal fun SubscriptionsRoute(
    subscriptionsViewModel: SubscriptionsViewModel = hiltViewModel(),
    subscriptionDialogViewModel: SubscriptionDialogViewModel = hiltViewModel(),
) {
    val subscriptionsState by subscriptionsViewModel.subscriptionsUiState.collectAsStateWithLifecycle()

    SubscriptionsScreen(
        subscriptionsState = subscriptionsState,
        onSubscriptionEvent = subscriptionDialogViewModel::onSubscriptionEvent,
    )
}

@Composable
internal fun SubscriptionsScreen(
    subscriptionsState: SubscriptionsUiState,
    onSubscriptionEvent: (SubscriptionDialogEvent) -> Unit,
) {
    var showSubscriptionBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showSubscriptionDialog by rememberSaveable { mutableStateOf(false) }

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
                            modifier = Modifier.clickable {
                                onSubscriptionEvent(SubscriptionDialogEvent.UpdateId(subscription.id))
                                showSubscriptionBottomSheet = true
                            }
                        )
                    }
                }
                if (showSubscriptionBottomSheet) {
                    SubscriptionBottomSheet(
                        onDismiss = { showSubscriptionBottomSheet = false },
                        onEdit = { showSubscriptionDialog = true },
                    )
                }
                if (showSubscriptionDialog) {
                    SubscriptionDialog(
                        onDismiss = { showSubscriptionDialog = false },
                    )
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