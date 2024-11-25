package ru.resodostudios.cashsense.ui.home2pane

import androidx.activity.compose.BackHandler
import androidx.annotation.Keep
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
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
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.R
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.util.Constants.DEEP_LINK_SCHEME_AND_HOST
import ru.resodostudios.cashsense.core.util.Constants.HOME_PATH
import ru.resodostudios.cashsense.core.util.Constants.OPEN_TRANSACTION_DIALOG_KEY
import ru.resodostudios.cashsense.core.util.Constants.WALLET_ID_KEY
import ru.resodostudios.cashsense.feature.home.HomeScreen
import ru.resodostudios.cashsense.feature.home.navigation.HomeRoute
import ru.resodostudios.cashsense.feature.wallet.detail.navigation.WalletRoute
import ru.resodostudios.cashsense.feature.wallet.detail.navigation.navigateToWallet
import ru.resodostudios.cashsense.feature.wallet.detail.navigation.walletScreen
import kotlin.uuid.Uuid
import ru.resodostudios.cashsense.core.locales.R as localesR

private const val DEEP_LINK_BASE_PATH = "$DEEP_LINK_SCHEME_AND_HOST/$HOME_PATH/{$WALLET_ID_KEY}/{$OPEN_TRANSACTION_DIALOG_KEY}"

// TODO: Remove @Keep when https://issuetracker.google.com/353898971 is fixed
@Keep
@Serializable
internal object WalletPlaceholderRoute

@Keep
@Serializable
internal object DetailPaneNavHostRoute

@Serializable
internal object HomeListDetailRoute

fun NavGraphBuilder.homeListDetailScreen(
    onEditWallet: (String) -> Unit,
    onTransfer: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    navigateToTransactionDialog: (walletId: String, transactionId: String?, repeated: Boolean) -> Unit,
    nestedDestinations: NavGraphBuilder.() -> Unit,
) {
    navigation<HomeListDetailRoute>(startDestination = HomeRoute()) {
        composable<HomeRoute>(
            deepLinks = listOf(
                navDeepLink<HomeRoute>(basePath = DEEP_LINK_BASE_PATH),
            ),
        ) {
            HomeListDetailScreen(
                onEditWallet = onEditWallet,
                onTransfer = onTransfer,
                onShowSnackbar = onShowSnackbar,
                navigateToTransactionDialog = navigateToTransactionDialog,
            )
        }
        nestedDestinations()
    }
}

@Composable
internal fun HomeListDetailScreen(
    onEditWallet: (String) -> Unit,
    onTransfer: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    navigateToTransactionDialog: (walletId: String, transactionId: String?, repeated: Boolean) -> Unit,
    viewModel: Home2PaneViewModel = hiltViewModel(),
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    val selectedWalletId by viewModel.selectedWalletId.collectAsStateWithLifecycle()
    val shouldDisplayUndoWallet by viewModel.shouldDisplayUndoWalletState.collectAsStateWithLifecycle()

    HomeListDetailScreen(
        selectedWalletId = selectedWalletId,
        navigateToTransactionDialog = navigateToTransactionDialog,
        onWalletClick = viewModel::onWalletClick,
        onTransfer = onTransfer,
        onEditWallet = onEditWallet,
        onDeleteWallet = viewModel::deleteWallet,
        onShowSnackbar = onShowSnackbar,
        windowAdaptiveInfo = windowAdaptiveInfo,
        shouldDisplayUndoWallet = shouldDisplayUndoWallet,
        undoWalletRemoval = viewModel::undoWalletRemoval,
        clearUndoState = viewModel::clearUndoState,
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun HomeListDetailScreen(
    selectedWalletId: String?,
    navigateToTransactionDialog: (walletId: String, transactionId: String?, repeated: Boolean) -> Unit,
    onWalletClick: (String) -> Unit,
    onTransfer: (String) -> Unit,
    onEditWallet: (String) -> Unit,
    onDeleteWallet: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    windowAdaptiveInfo: WindowAdaptiveInfo,
    shouldDisplayUndoWallet: Boolean = false,
    undoWalletRemoval: () -> Unit = {},
    clearUndoState: () -> Unit = {},
) {
    val listDetailNavigator = rememberListDetailPaneScaffoldNavigator(
        scaffoldDirective = calculatePaneScaffoldDirective(windowAdaptiveInfo),
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
        val route = selectedWalletId?.let { WalletRoute(it) } ?: WalletPlaceholderRoute
        mutableStateOf(route)
    }
    var nestedNavKey by rememberSaveable(
        stateSaver = Saver({ it.toString() }, Uuid::parse),
    ) {
        mutableStateOf(Uuid.random())
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
                nestedNavHostStartRoute = WalletRoute(walletId)
                nestedNavKey = Uuid.random()
                clearUndoState()
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
                    onTransfer = onTransfer,
                    onEditWallet = onEditWallet,
                    onDeleteWallet = onDeleteWallet,
                    onTransactionCreate = ::onWalletClickShowDetailPane,
                    highlightSelectedWallet = listDetailNavigator.isDetailPaneVisible(),
                    onShowSnackbar = onShowSnackbar,
                    shouldDisplayUndoWallet = shouldDisplayUndoWallet,
                    undoWalletRemoval = undoWalletRemoval,
                    clearUndoState = clearUndoState,
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
                            showNavigationIcon = !listDetailNavigator.isListPaneVisible(),
                            onEditWallet = onEditWallet,
                            onDeleteWallet = {
                                onDeleteWallet(it)
                                if (listDetailNavigator.isDetailPaneVisible()) {
                                    nestedNavController.navigate(WalletPlaceholderRoute)
                                }
                                listDetailNavigator.navigateTo(ListDetailPaneScaffoldRole.List)
                            },
                            onTransfer = onTransfer,
                            onBackClick = listDetailNavigator::navigateBack,
                            navigateToTransactionDialog = navigateToTransactionDialog,
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