package ru.resodostudios.cashsense.feature.wallet.detail.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.resodostudios.cashsense.feature.wallet.detail.WalletRoute

internal const val WALLET_ID_ARG = "walletId"
const val WALLET_ROUTE = "wallet_route"

internal class WalletArgs(val walletId: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(checkNotNull(savedStateHandle[WALLET_ID_ARG]).toString())
}

fun NavController.navigateToWallet(
    walletId: String,
    navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    val walletRoute = "$WALLET_ROUTE/$walletId"
    navigate(walletRoute) {
        navOptions()
    }
}

fun NavGraphBuilder.walletScreen(
    showBackButton: Boolean,
    onBackClick: () -> Unit,
) {
    composable(
        route = "$WALLET_ROUTE/{$WALLET_ID_ARG}",
        arguments = listOf(
            navArgument(WALLET_ID_ARG) { type = NavType.StringType }
        )
    ) {
        WalletRoute(
            onBackClick = onBackClick,
        )
    }
}