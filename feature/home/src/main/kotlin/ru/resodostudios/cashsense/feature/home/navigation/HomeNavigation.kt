package ru.resodostudios.cashsense.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ru.resodostudios.cashsense.feature.home.HomeRoute

const val HOME_GRAPH_ROUTE_PATTERN = "home_graph"
const val homeRoute = "home_route"

fun NavController.navigateToHomeGraph(navOptions: NavOptions? = null) {
    this.navigate(HOME_GRAPH_ROUTE_PATTERN, navOptions)
}

fun NavGraphBuilder.homeGraph(
    onWalletClick: (Long) -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit
) {
    navigation(
        route = HOME_GRAPH_ROUTE_PATTERN,
        startDestination = homeRoute,
    ) {
        composable(route = homeRoute) {
            HomeRoute(onWalletClick)
        }
        nestedGraphs()
    }
}