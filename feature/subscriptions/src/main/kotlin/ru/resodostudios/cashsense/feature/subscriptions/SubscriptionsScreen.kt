package ru.resodostudios.cashsense.feature.subscriptions

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
import ru.resodostudios.cashsense.core.model.data.Subscription
import ru.resodostudios.cashsense.core.ui.EditAndDeleteDropdownMenu
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.formatAmountWithCurrency
import ru.resodostudios.cashsense.core.ui.formattedDate

@Composable
internal fun SubscriptionsRoute(
    onEdit: (String) -> Unit,
    viewModel: SubscriptionsViewModel = hiltViewModel()
) {
    val subscriptionsState by viewModel.subscriptionsUiState.collectAsStateWithLifecycle()

    SubscriptionsScreen(
        subscriptionsState = subscriptionsState,
        onEdit = onEdit,
        onDelete = viewModel::deleteSubscription
    )
}

@Composable
internal fun SubscriptionsScreen(
    subscriptionsState: SubscriptionsUiState,
    onEdit: (String) -> Unit,
    onDelete: (Subscription) -> Unit
) {
    when (subscriptionsState) {
        SubscriptionsUiState.Loading -> LoadingState()
        is SubscriptionsUiState.Success -> {
            if (subscriptionsState.subscriptions.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(300.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(subscriptionsState.subscriptions) { subscription ->
                        ListItem(
                            headlineContent = { Text(subscription.title) },
                            supportingContent = { Text(subscription.amount.formatAmountWithCurrency(subscription.currency)) },
                            overlineContent = { Text(formattedDate(subscription.paymentDate)) },
                            trailingContent = {
                                EditAndDeleteDropdownMenu(
                                    onEdit = { onEdit(subscription.id) },
                                    onDelete = { onDelete(subscription) }
                                )
                            }
                        )
                    }
                }
            } else {
                EmptyState(
                    messageRes = R.string.feature_subscriptions_empty_message,
                    animationRes = R.raw.anim_empty_state
                )
            }
        }
    }
}