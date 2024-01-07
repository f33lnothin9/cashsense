package ru.resodostudios.cashsense.feature.subscriptions.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ru.resodostudios.cashsense.feature.subscriptions.AddSubscriptionRoute
import ru.resodostudios.cashsense.feature.subscriptions.SubscriptionsRoute

private const val SUBSCRIPTIONS_GRAPH_ROUTE_PATTERN = "subscriptions_graph"
const val SUBSCRIPTIONS_ROUTE = "subscriptions_route"
const val ADD_SUBSCRIPTION_ROUTE = "add_subscription_route"

fun NavController.navigateToSubscriptionsGraph(navOptions: NavOptions? = null) = navigate(SUBSCRIPTIONS_GRAPH_ROUTE_PATTERN, navOptions)
fun NavController.navigateToAddSubscription() = navigate(ADD_SUBSCRIPTION_ROUTE) {
    launchSingleTop = true
}

fun NavGraphBuilder.subscriptionsGraph(
    nestedGraphs: NavGraphBuilder.() -> Unit
) {
    navigation(
        route = SUBSCRIPTIONS_GRAPH_ROUTE_PATTERN,
        startDestination = SUBSCRIPTIONS_ROUTE
    ) {
        composable(
            route = SUBSCRIPTIONS_ROUTE
        ) {
            SubscriptionsRoute()
        }
        nestedGraphs()
    }
}

fun NavGraphBuilder.addSubscriptionScreen(
    onBackClick: () -> Unit
) {
    composable(
        route = ADD_SUBSCRIPTION_ROUTE
    ) {
        AddSubscriptionRoute(
            onBackClick = onBackClick
        )
    }
}