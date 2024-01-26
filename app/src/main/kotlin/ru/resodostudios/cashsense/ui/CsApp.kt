package ru.resodostudios.cashsense.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigation.suite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import ru.resodostudios.cashsense.R
import ru.resodostudios.cashsense.core.designsystem.component.CsTopAppBar
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.feature.categories.AddCategoryDialog
import ru.resodostudios.cashsense.feature.settings.SettingsBottomSheet
import ru.resodostudios.cashsense.feature.subscription.navigation.navigateToSubscription
import ru.resodostudios.cashsense.feature.wallet.AddWalletDialog
import ru.resodostudios.cashsense.navigation.CsNavHost
import ru.resodostudios.cashsense.navigation.TopLevelDestination
import ru.resodostudios.cashsense.navigation.TopLevelDestination.CATEGORIES
import ru.resodostudios.cashsense.navigation.TopLevelDestination.HOME
import ru.resodostudios.cashsense.navigation.TopLevelDestination.SUBSCRIPTIONS
import ru.resodostudios.cashsense.feature.categories.R as categoriesR
import ru.resodostudios.cashsense.feature.subscription.R as subscriptionsR
import ru.resodostudios.cashsense.feature.wallet.R as walletR

@OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
@Composable
fun CsApp(
    windowSize: DpSize,
    appState: CsAppState = rememberCsAppState(
        windowSize = windowSize
    )
) {
    var showSettingsBottomSheet by rememberSaveable {
        mutableStateOf(false)
    }
    var showAddCategoryDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showAddWalletDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (showSettingsBottomSheet) {
        SettingsBottomSheet(
            onDismiss = { showSettingsBottomSheet = false }
        )
    }
    if (showAddCategoryDialog) {
        AddCategoryDialog(
            onDismiss = { showAddCategoryDialog = false }
        )
    }
    if (showAddWalletDialog) {
        AddWalletDialog(
            onDismiss = { showAddWalletDialog = false }
        )
    }

    val currentDestination = appState.currentDestination

    NavigationSuiteScaffold(
        layoutType = appState.navigationSuiteType,
        navigationSuiteItems = {
            appState.topLevelDestinations.forEach { destination ->
                val isSelected = currentDestination.isTopLevelDestinationInHierarchy(destination)
                item(
                    selected = isSelected,
                    icon = {
                        Icon(
                            imageVector = if (isSelected) {
                                ImageVector.vectorResource(destination.selectedIcon)
                            } else {
                                ImageVector.vectorResource(destination.unselectedIcon)
                            },
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(destination.iconTextId)) },
                    onClick = { appState.navigateToTopLevelDestination(destination) }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                val destination = appState.currentTopLevelDestination
                if (destination != null) {
                    CsTopAppBar(
                        titleRes = destination.titleTextId,
                        actionIconRes = CsIcons.Settings,
                        actionIconContentDescription = stringResource(R.string.top_app_bar_action_icon_description),
                        onActionClick = { showSettingsBottomSheet = true }
                    )
                }
            },
            floatingActionButton = {
                when (appState.currentTopLevelDestination) {
                    HOME -> {
                        ExtendedFloatingActionButton(
                            onClick = { showAddWalletDialog = true }
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(CsIcons.Wallet),
                                contentDescription = null
                            )
                            Text(
                                text = stringResource(walletR.string.feature_wallet_new_wallet),
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }

                    CATEGORIES -> {
                        FloatingActionButton(
                            onClick = { showAddCategoryDialog = true }
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(CsIcons.Add),
                                contentDescription = stringResource(categoriesR.string.add_category_icon_description)
                            )
                        }
                    }

                    SUBSCRIPTIONS -> {
                        FloatingActionButton(
                            onClick = { appState.navController.navigateToSubscription(" ") }
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(CsIcons.Add),
                                contentDescription = stringResource(subscriptionsR.string.feature_subscription_add_subscription_icon_description)
                            )
                        }
                    }

                    null -> {}
                }
            },
            contentWindowInsets = if (appState.navigationSuiteType == NavigationSuiteType.NavigationBar) {
                WindowInsets(0, 0, 0, 0)
            } else {
                WindowInsets.navigationBars
            }
        ) { padding ->
            CsNavHost(
                appState = appState,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false