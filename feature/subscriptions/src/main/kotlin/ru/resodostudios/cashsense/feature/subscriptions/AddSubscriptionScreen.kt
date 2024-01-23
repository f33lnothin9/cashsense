package ru.resodostudios.cashsense.feature.subscriptions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.ui.validateAmount

@Composable
internal fun AddSubscriptionRoute(
    onBackClick: () -> Unit,
    viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val subscriptionUiState by viewModel.subscriptionUiState.collectAsStateWithLifecycle()
    
    AddSubscriptionScreen(
        subscriptionUiState = subscriptionUiState,
        onSubscriptionEvent = viewModel::onSubscriptionEvent,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddSubscriptionScreen(
    subscriptionUiState: SubscriptionUiState,
    onSubscriptionEvent: (SubscriptionEvent) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.feature_subscriptions_new_subscription)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = ImageVector.vectorResource(CsIcons.ArrowBack),
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            onSubscriptionEvent(SubscriptionEvent.Confirm)
                            onBackClick()
                        },
                        enabled = subscriptionUiState.title.isNotBlank() && subscriptionUiState.amount.validateAmount().second && subscriptionUiState.paymentDate.isNotBlank()
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(CsIcons.Confirm),
                            contentDescription = stringResource(R.string.feature_subscriptions_add_subscription_icon_description)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = paddingValues,
            modifier = Modifier.padding(16.dp)
        ) {
            subscriptionForm(
                subscriptionUiState = subscriptionUiState,
                onSubscriptionEvent = onSubscriptionEvent
            )
        }
    }
}