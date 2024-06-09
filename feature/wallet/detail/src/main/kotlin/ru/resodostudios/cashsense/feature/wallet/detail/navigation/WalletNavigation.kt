package ru.resodostudios.cashsense.feature.wallet.detail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.wallet.detail.WalletScreen

@Serializable
data class WalletRoute(val id: String)

fun NavController.navigateToWallet(
    walletId: String,
    navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(route = WalletRoute(walletId)) {
        navOptions()
    }
}

fun NavGraphBuilder.walletScreen(
    showDetailActions: Boolean,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    openTransactionDialog: Boolean,
    onTransactionDialogDismiss: () -> Unit = {},
) {
    composable<WalletRoute> {
        WalletScreen(
            showDetailActions = showDetailActions,
            onBackClick = onBackClick,
            onShowSnackbar = onShowSnackbar,
            openTransactionDialog = openTransactionDialog,
            onTransactionDialogDismiss = onTransactionDialogDismiss,
        )
    }
}