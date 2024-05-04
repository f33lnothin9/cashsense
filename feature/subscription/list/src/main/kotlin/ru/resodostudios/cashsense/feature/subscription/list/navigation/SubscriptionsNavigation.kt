package ru.resodostudios.cashsense.feature.subscription.list.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.subscription.list.SubscriptionsScreen

@Serializable
object SubscriptionsDestination

fun NavController.navigateToSubscriptions(navOptions: NavOptions) =
    navigate(route = SubscriptionsDestination, navOptions)

fun NavGraphBuilder.subscriptionsScreen() {
    composable<SubscriptionsDestination> {
        SubscriptionsScreen()
    }
}