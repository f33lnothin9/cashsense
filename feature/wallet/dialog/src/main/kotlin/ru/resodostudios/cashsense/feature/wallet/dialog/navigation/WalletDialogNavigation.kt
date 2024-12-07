package ru.resodostudios.cashsense.feature.wallet.dialog.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.dialog
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialog

@Serializable
data class WalletDialogRoute(
    val walletId: String? = null,
)

fun NavController.navigateToWalletDialog(
    walletId: String? = null,
    navOptions: NavOptionsBuilder.() -> Unit = {},
) = navigate(route = WalletDialogRoute(walletId)) {
    navOptions()
}

fun NavGraphBuilder.walletDialog(
    onDismiss: () -> Unit,
) {
    dialog<WalletDialogRoute> {
        WalletDialog(
            onDismiss = onDismiss,
        )
    }
}