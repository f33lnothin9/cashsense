package ru.resodostudios.cashsense.feature.wallet.detail.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.wallet.detail.WalletScreen

@Serializable
data class WalletDestination(val id: String)

fun NavController.navigateToWallet(
    walletId: String,
    navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(route = WalletDestination(walletId)) {
        navOptions()
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun NavGraphBuilder.walletScreen(
    showDetailActions: Boolean,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    threePaneScaffoldScope: ThreePaneScaffoldScope,
) {
    composable<WalletDestination> {
        threePaneScaffoldScope.AnimatedPane {
            WalletScreen(
                showDetailActions = showDetailActions,
                onBackClick = onBackClick,
                onShowSnackbar = onShowSnackbar,
            )
        }
    }
}