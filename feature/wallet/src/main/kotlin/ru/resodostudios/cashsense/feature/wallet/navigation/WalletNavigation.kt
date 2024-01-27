package ru.resodostudios.cashsense.feature.wallet.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.resodostudios.cashsense.feature.wallet.WalletRoute

internal const val walletIdArg = "walletId"

internal class WalletArgs(val walletId: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(savedStateHandle.get<String>(walletIdArg).toString())
}

fun NavController.navigateToWallet(walletId: String) = navigate("wallet_route/$walletId") {
    launchSingleTop = true
}

fun NavGraphBuilder.walletScreen(
    onBackClick: () -> Unit
) {
    composable(
        route = "wallet_route/{$walletIdArg}",
        arguments = listOf(
            navArgument(walletIdArg) { type = NavType.StringType }
        )
    ) {
        WalletRoute(
            onBackClick = onBackClick
        )
    }
}