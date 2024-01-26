package ru.resodostudios.cashsense.feature.subscriptions.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import ru.resodostudios.cashsense.feature.subscriptions.SubscriptionRoute
import ru.resodostudios.cashsense.feature.subscriptions.SubscriptionsRoute

internal const val SUBSCRIPTION_ID_ARG = "subscriptionId"

private const val SUBSCRIPTIONS_GRAPH_ROUTE_PATTERN = "subscriptions_graph"
const val SUBSCRIPTIONS_ROUTE = "subscriptions_route"
const val SUBSCRIPTION_ROUTE = "subscription_route"

internal class SubscriptionArgs(val subscriptionId: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(savedStateHandle.get<String>(SUBSCRIPTION_ID_ARG) ?: "")
}

fun NavController.navigateToSubscriptionsGraph(navOptions: NavOptions? = null) =
    navigate(SUBSCRIPTIONS_GRAPH_ROUTE_PATTERN, navOptions)

fun NavController.navigateToSubscription(subscriptionId: String) = navigate("$SUBSCRIPTION_ROUTE/$subscriptionId") {
    launchSingleTop = true
}

fun NavGraphBuilder.subscriptionsGraph(
    onEdit: (String) -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit
) {
    navigation(
        route = SUBSCRIPTIONS_GRAPH_ROUTE_PATTERN,
        startDestination = SUBSCRIPTIONS_ROUTE
    ) {
        composable(
            route = SUBSCRIPTIONS_ROUTE
        ) {
            SubscriptionsRoute(
                onEdit = onEdit
            )
        }
        nestedGraphs()
    }
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