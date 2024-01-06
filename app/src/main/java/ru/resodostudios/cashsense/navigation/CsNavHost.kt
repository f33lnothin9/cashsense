package ru.resodostudios.cashsense.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import ru.resodostudios.cashsense.feature.categories.navigation.categoriesScreen
import ru.resodostudios.cashsense.feature.home.navigation.HOME_GRAPH_ROUTE_PATTERN
import ru.resodostudios.cashsense.feature.home.navigation.homeGraph
import ru.resodostudios.cashsense.feature.subscriptions.navigation.subscriptionsGraph
import ru.resodostudios.cashsense.feature.wallet.navigation.navigateToWallet
import ru.resodostudios.cashsense.feature.wallet.navigation.walletScreen
import ru.resodostudios.cashsense.ui.CsAppState

@Composable
fun CsNavHost(
    appState: CsAppState,
    modifier: Modifier = Modifier,
    startDestination: String = HOME_GRAPH_ROUTE_PATTERN,
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        homeGraph(
            onWalletClick = navController::navigateToWallet,
            nestedGraphs = {
                walletScreen(
                    onBackClick = navController::popBackStack
                )
            }
        )
        categoriesScreen()
        subscriptionsGraph(
            nestedGraphs = {

            }
        )
    }
}