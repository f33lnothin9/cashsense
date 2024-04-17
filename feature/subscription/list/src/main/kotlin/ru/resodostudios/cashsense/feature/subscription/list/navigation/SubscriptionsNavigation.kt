package ru.resodostudios.cashsense.feature.subscription.list.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.resodostudios.cashsense.feature.subscription.list.SubscriptionsRoute

const val SUBSCRIPTIONS_ROUTE = "subscriptions_route"

fun NavController.navigateToSubscriptions(navOptions: NavOptions? = null) = navigate(SUBSCRIPTIONS_ROUTE, navOptions)

fun NavGraphBuilder.subscriptionsScreen() {
    composable(
        route = SUBSCRIPTIONS_ROUTE
    ) {
        SubscriptionsRoute()
    }
}