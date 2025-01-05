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
    onEditWallet: (String) -> Unit,
    onDeleteWallet: (String) -> Unit,
    onTransfer: (String) -> Unit,
    onBackClick: () -> Unit,
    navigateToTransactionDialog: (walletId: String, transactionId: String?, repeated: Boolean) -> Unit,
) {
    composable<WalletRoute> {
        WalletScreen(
            showNavigationIcon = showNavigationIcon,
            onEditWallet = onEditWallet,
            onDeleteClick = onDeleteWallet,
            onTransfer = onTransfer,
            onBackClick = onBackClick,
            navigateToTransactionDialog = navigateToTransactionDialog,
        )
    }
}