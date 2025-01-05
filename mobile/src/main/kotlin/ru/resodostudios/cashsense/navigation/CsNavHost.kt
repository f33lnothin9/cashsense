package ru.resodostudios.cashsense.navigation

import androidx.compose.animation.core.snap
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import ru.resodostudios.cashsense.feature.category.dialog.navigation.categoryDialog
import ru.resodostudios.cashsense.feature.category.dialog.navigation.navigateToCategoryDialog
import ru.resodostudios.cashsense.feature.category.list.navigation.categoriesSection
import ru.resodostudios.cashsense.feature.settings.navigation.licensesScreen
import ru.resodostudios.cashsense.feature.settings.navigation.navigateToLicenses
import ru.resodostudios.cashsense.feature.settings.navigation.settingsSection
import ru.resodostudios.cashsense.feature.subscription.dialog.navigation.navigateToSubscriptionDialog
import ru.resodostudios.cashsense.feature.subscription.dialog.navigation.subscriptionDialog
import ru.resodostudios.cashsense.feature.subscription.list.navigation.subscriptionsSection
import ru.resodostudios.cashsense.feature.transaction.navigation.navigateToTransactionDialog
import ru.resodostudios.cashsense.feature.transaction.navigation.transactionDialog
import ru.resodostudios.cashsense.feature.transfer.navigation.navigateToTransferDialog
import ru.resodostudios.cashsense.feature.transfer.navigation.transferDialog
import ru.resodostudios.cashsense.feature.wallet.dialog.navigation.navigateToWalletDialog
import ru.resodostudios.cashsense.feature.wallet.dialog.navigation.walletDialog
import ru.resodostudios.cashsense.ui.CsAppState
import ru.resodostudios.cashsense.ui.home2pane.HomeListDetailRoute
import ru.resodostudios.cashsense.ui.home2pane.homeListDetailScreen

@Composable
fun CsNavHost(
    appState: CsAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = HomeListDetailRoute,
        modifier = modifier,
        enterTransition = { slideInVertically { it / 16 } + fadeIn() },
        exitTransition = { fadeOut(snap()) },
        popEnterTransition = { slideInVertically { it / 16 } + fadeIn() },
        popExitTransition = { fadeOut(snap()) },
    ) {
        homeListDetailScreen(
            onEditWallet = navController::navigateToWalletDialog,
            onTransfer = navController::navigateToTransferDialog,
            navigateToTransactionDialog = navController::navigateToTransactionDialog,
            onShowSnackbar = onShowSnackbar,
            nestedDestinations = {
                walletDialog(navController::navigateUp)
                transferDialog(navController::navigateUp)
                transactionDialog(navController::navigateUp)
            },
        )
        categoriesSection(
            onEditCategory = navController::navigateToCategoryDialog,
            onShowSnackbar = onShowSnackbar,
            nestedGraphs = {
                categoryDialog(navController::navigateUp)
            },
        )
        subscriptionsSection(
            onEditSubscription = navController::navigateToSubscriptionDialog,
            onShowSnackbar = onShowSnackbar,
            nestedGraphs = {
                subscriptionDialog(navController::navigateUp)
            },
        )
        settingsSection(
            onLicensesClick = navController::navigateToLicenses,
            nestedGraphs = { licensesScreen(navController::navigateUp) },
        )
    }
}