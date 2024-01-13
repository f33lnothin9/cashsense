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
import ru.resodostudios.cashsense.core.ui.formattedDate
import ru.resodostudios.cashsense.core.ui.getFormattedAmountAndCurrency

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
                            headlineContent = {
                                Text(text = subscription.title)
                            },
                            supportingContent = {
                                Text(
                                    text = getFormattedAmountAndCurrency(
                                        amount = subscription.amount.toDouble(),
                                        currencyName = subscription.currency
                                    )
                                )
                            },
                            overlineContent = {
                                Text(text = formattedDate(date = subscription.paymentDate))
                            },
                            trailingContent = {
                                EditAndDeleteDropdownMenu(
                                    onEdit = { onEdit(subscription.subscriptionId.toString()) },
                                    onDelete = { onDelete(subscription) }
                                )
                            }
                        )
                    }
                }
            } else {
                EmptyState(
                    messageId = R.string.feature_subscriptions_empty_message,
                    animationId = R.raw.anim_empty_state
                )
            }
        }
    }
}