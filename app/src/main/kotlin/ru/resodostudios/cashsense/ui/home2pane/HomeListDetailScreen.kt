package ru.resodostudios.cashsense.ui.home2pane

import androidx.activity.compose.BackHandler
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.feature.home.HomeScreen
import ru.resodostudios.cashsense.feature.home.navigation.HomeDestination
import ru.resodostudios.cashsense.feature.wallet.detail.R
import ru.resodostudios.cashsense.feature.wallet.detail.navigation.navigateToWallet
import ru.resodostudios.cashsense.feature.wallet.detail.navigation.walletScreen

@Serializable
object WalletPlaceholderDestination

@Serializable
object DetailPaneNavHostDestination

fun NavGraphBuilder.homeListDetailScreen(
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable<HomeDestination> { backStackEntry ->
        val walletIdArgument = backStackEntry.toRoute<HomeDestination>().walletId
        var walletId: String? by remember { mutableStateOf(walletIdArgument) }

        HomeListDetailScreen(
            selectedWalletId = walletId,
            onWalletClick = { walletId = it },
            onShowSnackbar = onShowSnackbar,
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun HomeListDetailScreen(
    selectedWalletId: String?,
    onWalletClick: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    val listDetailNavigator = rememberListDetailPaneScaffoldNavigator()
    BackHandler(listDetailNavigator.canNavigateBack()) {
        listDetailNavigator.navigateBack()
    }

    val nestedNavController = rememberNavController()

    fun onWalletClickShowDetailPane(walletId: String?) {
        if (walletId != null) {
            onWalletClick(walletId)
            nestedNavController.navigateToWallet(walletId) {
                popUpTo<DetailPaneNavHostDestination>()
            }
            listDetailNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
        } else {
            nestedNavController.navigate(WalletPlaceholderDestination)
        }
    }

    ListDetailPaneScaffold(
        value = listDetailNavigator.scaffoldValue,
        directive = listDetailNavigator.scaffoldDirective,
        listPane = {
            AnimatedPane {
                HomeScreen(
                    onWalletClick = ::onWalletClickShowDetailPane,
                    onShowSnackbar = onShowSnackbar,
                    highlightSelectedWallet = listDetailNavigator.isDetailPaneVisible(),
                )
            }
        },
        detailPane = {
            NavHost(
                navController = nestedNavController,
                startDestination = WalletPlaceholderDestination::class,
                route = DetailPaneNavHostDestination::class,
            ) {
                walletScreen(
                    showDetailActions = !listDetailNavigator.isListPaneVisible(),
                    onBackClick = listDetailNavigator::navigateBack,
                    onShowSnackbar = onShowSnackbar,
                    threePaneScaffoldScope = this@ListDetailPaneScaffold,
                )
                composable<WalletPlaceholderDestination> {
                    EmptyState(
                        messageRes = R.string.feature_wallet_detail_select_wallet,
                        animationRes = R.raw.anim_select_wallet,
                    )
                }
            }
        },
    )
    LaunchedEffect(Unit) {
        if (selectedWalletId != null) {
            onWalletClickShowDetailPane(selectedWalletId)
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun <T> ThreePaneScaffoldNavigator<T>.isListPaneVisible(): Boolean =
    scaffoldValue[ListDetailPaneScaffoldRole.List] == PaneAdaptedValue.Expanded

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun <T> ThreePaneScaffoldNavigator<T>.isDetailPaneVisible(): Boolean =
    scaffoldValue[ListDetailPaneScaffoldRole.Detail] == PaneAdaptedValue.Expanded