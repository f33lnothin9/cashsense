package ru.resodostudios.cashsense.feature.subscription.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.resodostudios.cashsense.feature.subscription.SubscriptionRoute

internal const val SUBSCRIPTION_ID_ARG = "subscriptionId"

const val SUBSCRIPTION_ROUTE = "subscription_route"

internal class SubscriptionArgs(val subscriptionId: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(savedStateHandle.get<String>(SUBSCRIPTION_ID_ARG) ?: "")
}

fun NavController.navigateToSubscription(subscriptionId: String) = navigate("$SUBSCRIPTION_ROUTE/$subscriptionId") {
    launchSingleTop = true
}

fun NavGraphBuilder.subscriptionScreen(
    onBackClick: () -> Unit
) {
    composable(
        route = "$SUBSCRIPTION_ROUTE/{$SUBSCRIPTION_ID_ARG}",
        arguments = listOf(
            navArgument(SUBSCRIPTION_ID_ARG) { type = NavType.StringType }
        )
    ) {
        SubscriptionRoute(
            onBackClick = onBackClick
        )
    }
}