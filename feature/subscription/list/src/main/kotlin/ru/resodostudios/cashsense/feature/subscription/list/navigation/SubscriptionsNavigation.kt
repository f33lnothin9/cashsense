package ru.resodostudios.cashsense.feature.subscription.list.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ru.resodostudios.cashsense.feature.subscription.list.SubscriptionsRoute

private const val SUBSCRIPTIONS_GRAPH_ROUTE_PATTERN = "subscriptions_graph"
const val SUBSCRIPTIONS_ROUTE = "subscriptions_route"

fun NavController.navigateToSubscriptionsGraph(navOptions: NavOptions? = null) =
    navigate(SUBSCRIPTIONS_GRAPH_ROUTE_PATTERN, navOptions)

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