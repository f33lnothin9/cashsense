package ru.resodostudios.cashsense.feature.subscription.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.feature.subscription.dialog.SubscriptionBottomSheet
import ru.resodostudios.cashsense.feature.subscription.dialog.SubscriptionDialog
import ru.resodostudios.cashsense.feature.subscription.dialog.SubscriptionDialogEvent
import ru.resodostudios.cashsense.feature.subscription.dialog.SubscriptionDialogViewModel
import ru.resodostudios.cashsense.core.ui.R as uiR

@Composable
internal fun SubscriptionsScreen(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    subscriptionsViewModel: SubscriptionsViewModel = hiltViewModel(),
    subscriptionDialogViewModel: SubscriptionDialogViewModel = hiltViewModel(),
) {
    val subscriptionsState by subscriptionsViewModel.subscriptionsUiState.collectAsStateWithLifecycle()

    SubscriptionsScreen(
        subscriptionsState = subscriptionsState,
        onShowSnackbar = onShowSnackbar,
        onSubscriptionEvent = subscriptionDialogViewModel::onSubscriptionEvent,
        hideSubscription = subscriptionsViewModel::hideSubscription,
        undoSubscriptionRemoval = subscriptionsViewModel::undoSubscriptionRemoval,
        clearUndoState = subscriptionsViewModel::clearUndoState,
    )
}

@Composable
internal fun SubscriptionsScreen(
    subscriptionsState: SubscriptionsUiState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onSubscriptionEvent: (SubscriptionDialogEvent) -> Unit,
    hideSubscription: (String) -> Unit = {},
    undoSubscriptionRemoval: () -> Unit = {},
    clearUndoState: () -> Unit = {},
) {
    var showSubscriptionBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showSubscriptionDialog by rememberSaveable { mutableStateOf(false) }

    when (subscriptionsState) {
        SubscriptionsUiState.Loading -> LoadingState(Modifier.fillMaxSize())
        is SubscriptionsUiState.Success -> {
            val subscriptionDeletedMessage = stringResource(R.string.feature_subscription_list_deleted)
            val undoText = stringResource(uiR.string.core_ui_undo)

            LaunchedEffect(subscriptionsState.shouldDisplayUndoSubscription) {
                if (subscriptionsState.shouldDisplayUndoSubscription) {
                    val snackBarResult = onShowSnackbar(subscriptionDeletedMessage, undoText)
                    if (snackBarResult) {
                        undoSubscriptionRemoval()
                    } else {
                        clearUndoState()
                    }
                }
            }
            LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
                clearUndoState()
            }

            if (subscriptionsState.subscriptions.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(300.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(subscriptionsState.subscriptions) { subscription ->
                        SubscriptionCard(
                            subscription = subscription,
                            onClick = { id ->
                                onSubscriptionEvent(SubscriptionDialogEvent.UpdateId(id))
                                showSubscriptionBottomSheet = true
                            },
                            modifier = Modifier.animateItem(),
                        )
                    }
                }
                if (showSubscriptionBottomSheet) {
                    SubscriptionBottomSheet(
                        onDismiss = { showSubscriptionBottomSheet = false },
                        onEdit = { showSubscriptionDialog = true },
                        onDelete = hideSubscription,
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