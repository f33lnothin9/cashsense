package ru.resodostudios.cashsense.feature.subscriptions.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val subscriptionsNavigationRoute = "subscriptions_route"

fun NavController.navigateToSubscriptions(navOptions: NavOptions? = null) {
    this.navigate(subscriptionsNavigationRoute, navOptions)
}

fun NavGraphBuilder.subscriptionsScreen() {
    composable(
        route = subscriptionsNavigationRoute
    ) {

    }
}