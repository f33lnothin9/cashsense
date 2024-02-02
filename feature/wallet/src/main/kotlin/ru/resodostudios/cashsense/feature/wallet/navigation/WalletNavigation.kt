package ru.resodostudios.cashsense.feature.wallet.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.resodostudios.cashsense.feature.wallet.WalletRoute

internal const val WALLET_ID_ARG = "walletId"

internal const val WALLET_ROUTE = "wallet_route"

internal class WalletArgs(val walletId: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(savedStateHandle.get<String>(WALLET_ID_ARG).toString())
}

fun NavController.navigateToWallet(walletId: String) = navigate("$WALLET_ROUTE/$walletId") {
    launchSingleTop = true
}

fun NavGraphBuilder.walletScreen(
    onBackClick: () -> Unit,
    onTransactionCreate: (String) -> Unit
) {
    composable(
        route = "$WALLET_ROUTE/{$WALLET_ID_ARG}",
        arguments = listOf(
            navArgument(WALLET_ID_ARG) { type = NavType.StringType }
        )
    ) {
        WalletRoute(
            onBackClick = onBackClick,
            onTransactionCreate = onTransactionCreate
        )
    }
}