package ru.resodostudios.cashsense.feature.wallet.edit.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.dialog
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.wallet.edit.EditWalletDialog

@Serializable
data class EditWalletRoute(
    val walletId: String? = null,
)

fun NavController.navigateToEditWallet(
    walletId: String? = null,
    navOptions: NavOptionsBuilder.() -> Unit = {},
) = navigate(route = EditWalletRoute(walletId)) {
    navOptions()
}

fun NavGraphBuilder.editWalletDialog(
    onDismiss: () -> Unit,
) {
    dialog<EditWalletRoute> {
        EditWalletDialog(
            onDismiss = onDismiss,
        )
    }
}