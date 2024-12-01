package ru.resodostudios.cashsense.feature.subscription.dialog.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.dialog
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.subscription.dialog.SubscriptionDialog

@Serializable
data class SubscriptionDialogRoute(
    val subscriptionId: String? = null,
)

fun NavController.navigateToSubscriptionDialog(
    subscriptionId: String? = null,
    navOptions: NavOptionsBuilder.() -> Unit = {},
) = navigate(route = SubscriptionDialogRoute(subscriptionId)) {
    navOptions()
}

fun NavGraphBuilder.subscriptionDialog(
    onDismiss: () -> Unit,
) {
    dialog<SubscriptionDialogRoute> {
        SubscriptionDialog(
            onDismiss = onDismiss,
        )
    }
}