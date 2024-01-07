package ru.resodostudios.cashsense.feature.subscriptions

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

@Composable
internal fun AddSubscriptionRoute(
    onBackClick: () -> Unit,
    viewModel: SubscriptionsViewModel = hiltViewModel()
) {
    AddSubscriptionScreen(
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddSubscriptionScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.feature_subscriptions_new_subscription)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = CsIcons.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = CsIcons.Confirm,
                            contentDescription = stringResource(R.string.feature_subscriptions_add_subscription_icon_description)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding
        ) {

        }
    }
}