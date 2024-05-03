package ru.resodostudios.cashsense.feature.wallet.detail.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldScope
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

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun NavGraphBuilder.walletScreen(
    showDetailActions: Boolean,
    onBackClick: () -> Unit,
    threePaneScaffoldScope: ThreePaneScaffoldScope,
) {
    composable(
        route = "$WALLET_ROUTE/{$WALLET_ID_ARG}",
        arguments = listOf(
            navArgument(WALLET_ID_ARG) { type = NavType.StringType }
        )
    ) {
        threePaneScaffoldScope.AnimatedPane {
            WalletRoute(
                showDetailActions = showDetailActions,
                onBackClick = onBackClick,
            )
        }
    }
}