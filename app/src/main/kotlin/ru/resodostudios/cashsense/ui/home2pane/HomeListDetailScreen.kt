package ru.resodostudios.cashsense.ui.home2pane

import androidx.activity.compose.BackHandler
import androidx.annotation.Keep
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.util.Constants.DEEP_LINK_SCHEME_AND_HOST
import ru.resodostudios.cashsense.core.util.Constants.HOME_PATH
import ru.resodostudios.cashsense.core.util.Constants.OPEN_TRANSACTION_DIALOG_KEY
import ru.resodostudios.cashsense.core.util.Constants.WALLET_ID_KEY
import ru.resodostudios.cashsense.feature.home.HomeScreen
import ru.resodostudios.cashsense.feature.home.navigation.HomeRoute
import ru.resodostudios.cashsense.feature.wallet.detail.R
import ru.resodostudios.cashsense.feature.wallet.detail.navigation.WalletRoute
import ru.resodostudios.cashsense.feature.wallet.detail.navigation.navigateToWallet
import ru.resodostudios.cashsense.feature.wallet.detail.navigation.walletScreen
import java.util.UUID
import ru.resodostudios.cashsense.core.locales.R as localesR

private const val DEEP_LINK_BASE_PATH = "$DEEP_LINK_SCHEME_AND_HOST/$HOME_PATH/{$WALLET_ID_KEY}/{$OPEN_TRANSACTION_DIALOG_KEY}"

// TODO: Remove @Keep when https://issuetracker.google.com/353898971 is fixed
@Keep
@Serializable
internal object WalletPlaceholderRoute

@Keep
@Serializable
internal object DetailPaneNavHostRoute

fun NavGraphBuilder.homeListDetailScreen(
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable<HomeRoute>(
        deepLinks = listOf(
            navDeepLink<HomeRoute>(basePath = DEEP_LINK_BASE_PATH),
        ),
    ) {
        HomeListDetailScreen(
            onShowSnackbar = onShowSnackbar,
        )
    }
}

@Composable
internal fun HomeListDetailScreen(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    viewModel: Home2PaneViewModel = hiltViewModel(),
) {
    val selectedWalletId by viewModel.selectedWalletId.collectAsStateWithLifecycle()
    val openTransactionDialog by viewModel.openTransactionDialog.collectAsStateWithLifecycle()

    HomeListDetailScreen(
        selectedWalletId = selectedWalletId,
        openTransactionDialog = openTransactionDialog,
        onWalletClick = viewModel::onWalletClick,
        onTransactionDialogDismiss = viewModel::onTransactionDialogDismiss,
        onShowSnackbar = onShowSnackbar,
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun HomeListDetailScreen(
    selectedWalletId: String?,
    openTransactionDialog: Boolean,
    onWalletClick: (String) -> Unit,
    onTransactionDialogDismiss: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    val listDetailNavigator = rememberListDetailPaneScaffoldNavigator(
        initialDestinationHistory = listOfNotNull(
            ThreePaneScaffoldDestinationItem(ListDetailPaneScaffoldRole.List),
            ThreePaneScaffoldDestinationItem<Nothing>(ListDetailPaneScaffoldRole.Detail).takeIf {
                selectedWalletId != null
            },
        ),
    )
    BackHandler(listDetailNavigator.canNavigateBack()) {
        listDetailNavigator.navigateBack()
    }

    var nestedNavHostStartRoute by remember {
        val route = selectedWalletId?.let { WalletRoute(walletId = it) } ?: WalletPlaceholderRoute
        mutableStateOf(route)
    }
    var nestedNavKey by rememberSaveable(
        stateSaver = Saver({ it.toString() }, UUID::fromString),
    ) {
        mutableStateOf(UUID.randomUUID())
    }
    val nestedNavController = key(nestedNavKey) {
        rememberNavController()
    }

    fun onWalletClickShowDetailPane(walletId: String?) {
        if (walletId != null) {
            onWalletClick(walletId)
            if (listDetailNavigator.isDetailPaneVisible()) {
                nestedNavController.navigateToWallet(walletId) {
                    popUpTo<DetailPaneNavHostRoute>()
                }
            } else {
                nestedNavHostStartRoute = WalletRoute(walletId = walletId)
                nestedNavKey = UUID.randomUUID()
            }
            listDetailNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
        } else if (listDetailNavigator.isDetailPaneVisible()) {
            nestedNavController.navigate(WalletPlaceholderRoute)
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
            AnimatedPane {
                key(nestedNavKey) {
                    NavHost(
                        navController = nestedNavController,
                        startDestination = nestedNavHostStartRoute,
                        route = DetailPaneNavHostRoute::class,
                    ) {
                        walletScreen(
                            showDetailActions = !listDetailNavigator.isListPaneVisible(),
                            onBackClick = listDetailNavigator::navigateBack,
                            onShowSnackbar = onShowSnackbar,
                            openTransactionDialog = openTransactionDialog,
                            onTransactionDialogDismiss = onTransactionDialogDismiss,
                        )
                        composable<WalletPlaceholderRoute> {
                            EmptyState(
                                messageRes = localesR.string.select_wallet,
                                animationRes = R.raw.anim_select_wallet,
                            )
                        }
                    }
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun <T> ThreePaneScaffoldNavigator<T>.isListPaneVisible(): Boolean =
    scaffoldValue[ListDetailPaneScaffoldRole.List] == PaneAdaptedValue.Expanded

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun <T> ThreePaneScaffoldNavigator<T>.isDetailPaneVisible(): Boolean =
    scaffoldValue[ListDetailPaneScaffoldRole.Detail] == PaneAdaptedValue.Expanded