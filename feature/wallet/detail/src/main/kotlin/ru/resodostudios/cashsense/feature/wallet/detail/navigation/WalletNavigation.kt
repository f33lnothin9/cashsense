package ru.resodostudios.cashsense.feature.wallet.detail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.wallet.detail.WalletScreen

@Serializable
data class WalletRoute(val walletId: String)

fun NavController.navigateToWallet(
    walletId: String,
    navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(route = WalletRoute(walletId)) {
        navOptions()
    }
}

fun NavGraphBuilder.walletScreen(
    showNavigationIcon: Boolean,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onWalletEdit: (String) -> Unit,
    openTransactionDialog: Boolean,
    setTransactionDialogOpen: (Boolean) -> Unit = {},
) {
    composable<WalletRoute> {
        WalletScreen(
            showNavigationIcon = showNavigationIcon,
            onBackClick = onBackClick,
            onShowSnackbar = onShowSnackbar,
            onWalletEdit = onWalletEdit,
            openTransactionDialog = openTransactionDialog,
            setTransactionDialogOpen = setTransactionDialogOpen,
        )
    }
}