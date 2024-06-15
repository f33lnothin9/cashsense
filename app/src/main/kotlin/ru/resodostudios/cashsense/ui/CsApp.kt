package ru.resodostudios.cashsense.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Short
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import ru.resodostudios.cashsense.R
import ru.resodostudios.cashsense.core.designsystem.component.CsFloatingActionButton
import ru.resodostudios.cashsense.core.designsystem.component.CsTopAppBar
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.feature.category.dialog.CategoryDialog
import ru.resodostudios.cashsense.feature.settings.SettingsBottomSheet
import ru.resodostudios.cashsense.feature.subscription.dialog.SubscriptionDialog
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialog
import ru.resodostudios.cashsense.navigation.CsNavHost
import ru.resodostudios.cashsense.navigation.TopLevelDestination
import ru.resodostudios.cashsense.navigation.TopLevelDestination.CATEGORIES
import ru.resodostudios.cashsense.navigation.TopLevelDestination.HOME
import ru.resodostudios.cashsense.navigation.TopLevelDestination.SUBSCRIPTIONS

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CsApp(
    appState: CsAppState,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    var showSettingsBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showCategoryDialog by rememberSaveable { mutableStateOf(false) }
    var showWalletDialog by rememberSaveable { mutableStateOf(false) }
    var showSubscriptionDialog by rememberSaveable { mutableStateOf(false) }

    if (showSettingsBottomSheet) {
        SettingsBottomSheet(onDismiss = { showSettingsBottomSheet = false })
    }
    if (showCategoryDialog) {
        CategoryDialog(onDismiss = { showCategoryDialog = false })
    }
    if (showWalletDialog) {
        WalletDialog(onDismiss = { showWalletDialog = false })
    }
    if (showSubscriptionDialog) {
        SubscriptionDialog(onDismiss = { showSubscriptionDialog = false })
    }

    val snackbarHostState = remember { SnackbarHostState() }

    val currentDestination = appState.currentDestination

    val layoutType = NavigationSuiteScaffoldDefaults
        .calculateFromAdaptiveInfo(windowAdaptiveInfo)

    NavigationSuiteScaffold(
        layoutType = layoutType,
        navigationSuiteItems = {
            appState.topLevelDestinations.forEach { destination ->
                val selected = currentDestination
                    .isTopLevelDestinationInHierarchy(destination)
                item(
                    selected = selected,
                    icon = {
                        Icon(
                            imageVector = if (selected) {
                                ImageVector.vectorResource(destination.selectedIcon)
                            } else {
                                ImageVector.vectorResource(destination.unselectedIcon)
                            },
                            contentDescription = null,
                        )
                    },
                    label = { Text(stringResource(destination.iconTextId)) },
                    onClick = { appState.navigateToTopLevelDestination(destination) },
                )
            }
        },
    ) {
        val destination = appState.currentTopLevelDestination

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                if (destination != null) {
                    CsFloatingActionButton(
                        titleRes = destination.fabTitle,
                        iconRes = destination.fabIcon,
                        onClick = {
                            when (destination) {
                                HOME -> showWalletDialog = true
                                CATEGORIES -> showCategoryDialog = true
                                SUBSCRIPTIONS -> showSubscriptionDialog = true
                            }
                        },
                        modifier = Modifier
                            .navigationBarsPadding()
                            .windowInsetsPadding(
                                WindowInsets.safeDrawing.only(
                                    WindowInsetsSides.Horizontal,
                                ),
                            ),
                    )
                }
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            modifier = Modifier.semantics {
                testTagsAsResourceId = true
            },
        ) { padding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Horizontal,
                        ),
                    ),
            ) {
                if (destination != null) {
                    CsTopAppBar(
                        titleRes = destination.titleTextId,
                        actionIconRes = CsIcons.Settings,
                        actionIconContentDescription = stringResource(R.string.top_app_bar_action_icon_description),
                        onActionClick = { showSettingsBottomSheet = true },
                    )
                }

                CsNavHost(
                    appState = appState,
                    onShowSnackbar = { message, action ->
                        snackbarHostState.showSnackbar(
                            message = message,
                            actionLabel = action,
                            duration = Short,
                        ) == ActionPerformed
                    },
                    modifier = Modifier.padding(padding),
                )
            }
        }
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.hasRoute(destination.route)
    } ?: false