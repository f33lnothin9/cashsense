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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.feature.home.HomeRoute
import ru.resodostudios.cashsense.feature.home.navigation.HOME_ROUTE
import ru.resodostudios.cashsense.feature.home.navigation.WALLET_ID_ARG
import ru.resodostudios.cashsense.feature.wallet.detail.R
import ru.resodostudios.cashsense.feature.wallet.detail.navigation.WALLET_ROUTE
import ru.resodostudios.cashsense.feature.wallet.detail.navigation.navigateToWallet
import ru.resodostudios.cashsense.feature.wallet.detail.navigation.walletScreen

private const val HOME_DETAIL_PANE_ROUTE = "home_detail_pane_route"

fun NavGraphBuilder.homeListDetailScreen() {
    composable(
        route = HOME_ROUTE,
        arguments = listOf(
            navArgument(WALLET_ID_ARG) {
                type = NavType.StringType
                defaultValue = null
                nullable = true
            },
        ),
    ) {
        HomeListDetailScreen()
    }
}

@Composable
internal fun HomeListDetailScreen(
    viewModel: Home2PaneViewModel = hiltViewModel(),
) {
    val selectedWalletId by viewModel.selectedWalletId.collectAsStateWithLifecycle()

    HomeListDetailScreen(
        selectedTopicId = selectedWalletId,
        onWalletClick = viewModel::onWalletClick,
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun HomeListDetailScreen(
    selectedTopicId: String?,
    onWalletClick: (String) -> Unit,
) {
    val listDetailNavigator = rememberListDetailPaneScaffoldNavigator()
    BackHandler(listDetailNavigator.canNavigateBack()) {
        listDetailNavigator.navigateBack()
    }

    val nestedNavController = rememberNavController()

    fun onWalletClickShowDetailPane(walletId: String) {
        onWalletClick(walletId)
        nestedNavController.navigateToWallet(walletId) {
            popUpTo(HOME_DETAIL_PANE_ROUTE)
        }
        listDetailNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
    }

    ListDetailPaneScaffold(
        value = listDetailNavigator.scaffoldValue,
        directive = listDetailNavigator.scaffoldDirective,
        listPane = {
            AnimatedPane {
                HomeRoute(
                    onWalletClick = ::onWalletClickShowDetailPane,
                    highlightSelectedWallet = listDetailNavigator.isDetailPaneVisible(),
                )
            }
        },
        detailPane = {
            NavHost(
                navController = nestedNavController,
                startDestination = WALLET_ROUTE,
                route = HOME_DETAIL_PANE_ROUTE,
            ) {
                walletScreen(
                    showDetailActions = !listDetailNavigator.isListPaneVisible(),
                    onBackClick = listDetailNavigator::navigateBack,
                    threePaneScaffoldScope = this@ListDetailPaneScaffold,
                )
                composable(route = WALLET_ROUTE) {
                    EmptyState(
                        messageRes = R.string.feature_wallet_detail_select_wallet,
                        animationRes = R.raw.anim_select_wallet,
                    )
                }
            }
        },
    )
    LaunchedEffect(Unit) {
        if (selectedTopicId != null) {
            onWalletClickShowDetailPane(selectedTopicId)
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun <T> ThreePaneScaffoldNavigator<T>.isListPaneVisible(): Boolean =
    scaffoldValue[ListDetailPaneScaffoldRole.List] == PaneAdaptedValue.Expanded

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun <T> ThreePaneScaffoldNavigator<T>.isDetailPaneVisible(): Boolean =
    scaffoldValue[ListDetailPaneScaffoldRole.Detail] == PaneAdaptedValue.Expanded