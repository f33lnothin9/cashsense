package ru.resodostudios.cashsense.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigation.suite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.google.android.play.core.appupdate.AppUpdateManager
import ru.resodostudios.cashsense.R
import ru.resodostudios.cashsense.core.designsystem.component.CsTopAppBar
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.feature.categories.AddCategoryDialog
import ru.resodostudios.cashsense.feature.settings.SettingsDialog
import ru.resodostudios.cashsense.feature.transaction.AddTransactionDialog
import ru.resodostudios.cashsense.feature.wallet.AddWalletDialog
import ru.resodostudios.cashsense.navigation.CsNavHost
import ru.resodostudios.cashsense.navigation.TopLevelDestination
import ru.resodostudios.cashsense.navigation.TopLevelDestination.CATEGORIES
import ru.resodostudios.cashsense.navigation.TopLevelDestination.HOME
import ru.resodostudios.cashsense.navigation.TopLevelDestination.SUBSCRIPTIONS
import ru.resodostudios.cashsense.feature.categories.R as categoriesR
import ru.resodostudios.cashsense.feature.wallet.R as walletR

@OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
@Composable
fun CsApp(
    appUpdateManager: AppUpdateManager,
    isUpdateDownloaded: Boolean,
    windowSize: DpSize,
    appState: CsAppState = rememberCsAppState(
        windowSize = windowSize
    )
) {
    var showSettingsDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showNewTransactionDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showAddCategoryDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showAddWalletDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(isUpdateDownloaded) {
        if (isUpdateDownloaded) {
            val snackbarResult = snackbarHostState.showSnackbar(
                message = "Update downloaded.",
                duration = SnackbarDuration.Indefinite,
                actionLabel = "Install",
                withDismissAction = true
            )
            if (snackbarResult == SnackbarResult.ActionPerformed) appUpdateManager.completeUpdate()
        }
    }

    if (showSettingsDialog) {
        SettingsDialog(
            onDismiss = { showSettingsDialog = false }
        )
    }
    if (showNewTransactionDialog) {
        AddTransactionDialog(
            onDismiss = { showNewTransactionDialog = false },
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
                            painter = if (isSelected) {
                                painterResource(destination.selectedIcon)
                            } else {
                                painterResource(destination.unselectedIcon)
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
                        actionIcon = CsIcons.Settings,
                        actionIconContentDescription = stringResource(R.string.top_app_bar_action_icon_description),
                        onActionClick = { showSettingsDialog = true }
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                when (appState.currentTopLevelDestination) {
                    HOME -> {
                        ExtendedFloatingActionButton(
                            onClick = { showAddWalletDialog = true }
                        ) {
                            Icon(
                                imageVector = CsIcons.Add,
                                contentDescription = null
                            )
                            Text(
                                text = stringResource(walletR.string.add_wallet),
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }

                    CATEGORIES -> {
                        FloatingActionButton(
                            onClick = { showAddCategoryDialog = true }
                        ) {
                            Icon(
                                imageVector = CsIcons.Add,
                                contentDescription = stringResource(categoriesR.string.add_category_icon_description)
                            )
                        }
                    }

                    SUBSCRIPTIONS -> {}
                    null -> {}
                }
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
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