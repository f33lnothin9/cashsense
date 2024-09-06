package ru.resodostudios.cashsense.feature.subscription.list.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.core.util.Constants.DEEP_LINK_SCHEME_AND_HOST
import ru.resodostudios.cashsense.core.util.Constants.SUBSCRIPTIONS_PATH
import ru.resodostudios.cashsense.feature.subscription.list.SubscriptionsScreen

@Serializable
object SubscriptionsRoute

private const val DEEP_LINK_URI_PATTERN = "$DEEP_LINK_SCHEME_AND_HOST/$SUBSCRIPTIONS_PATH"

fun NavController.navigateToSubscriptions(navOptions: NavOptions) =
    navigate(route = SubscriptionsRoute, navOptions)

fun NavGraphBuilder.subscriptionsScreen(
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable<SubscriptionsRoute>(
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        SubscriptionsScreen(onShowSnackbar)
    }
}