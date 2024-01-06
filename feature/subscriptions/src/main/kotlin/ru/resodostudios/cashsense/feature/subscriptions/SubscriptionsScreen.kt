package ru.resodostudios.cashsense.feature.subscriptions

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.ui.LoadingState

@Composable
internal fun SubscriptionsRoute(
    viewModel: SubscriptionsViewModel = hiltViewModel()
) {
    val subscriptionsState by viewModel.subscriptionsUiState.collectAsStateWithLifecycle()
    SubscriptionsScreen(
        subscriptionsState = subscriptionsState
    )
}

@Composable
internal fun SubscriptionsScreen(
    subscriptionsState: SubscriptionsUiState
) {
    when (subscriptionsState) {
        SubscriptionsUiState.Loading -> LoadingState()
        is SubscriptionsUiState.Success -> {
            if (subscriptionsState.subscriptions.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(300.dp)
                ) {

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