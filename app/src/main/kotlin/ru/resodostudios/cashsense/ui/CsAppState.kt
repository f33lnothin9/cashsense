package ru.resodostudios.cashsense.ui

import androidx.compose.material3.adaptive.navigationsuite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Expanded
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Medium
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone
import ru.resodostudios.cashsense.core.data.util.TimeZoneMonitor
import ru.resodostudios.cashsense.feature.category.list.navigation.CATEGORIES_ROUTE
import ru.resodostudios.cashsense.feature.category.list.navigation.navigateToCategories
import ru.resodostudios.cashsense.feature.home.navigation.HOME_ROUTE
import ru.resodostudios.cashsense.feature.home.navigation.navigateToHomeGraph
import ru.resodostudios.cashsense.feature.subscription.list.navigation.SUBSCRIPTIONS_ROUTE
import ru.resodostudios.cashsense.feature.subscription.list.navigation.navigateToSubscriptions
import ru.resodostudios.cashsense.navigation.TopLevelDestination
import ru.resodostudios.cashsense.navigation.TopLevelDestination.CATEGORIES
import ru.resodostudios.cashsense.navigation.TopLevelDestination.HOME
import ru.resodostudios.cashsense.navigation.TopLevelDestination.SUBSCRIPTIONS

@Composable
fun rememberCsAppState(
    windowSizeClass: WindowSizeClass,
    timeZoneMonitor: TimeZoneMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): CsAppState {

    return remember(
        windowSizeClass,
        timeZoneMonitor,
        coroutineScope,
        navController,
    ) {
        CsAppState(
            windowSizeClass = windowSizeClass,
            timeZoneMonitor = timeZoneMonitor,
            coroutineScope = coroutineScope,
            navController = navController,
        )
    }
}

@Stable
class CsAppState(
    windowSizeClass: WindowSizeClass,
    timeZoneMonitor: TimeZoneMonitor,
    coroutineScope: CoroutineScope,
    val navController: NavHostController,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            HOME_ROUTE -> HOME
            CATEGORIES_ROUTE -> CATEGORIES
            SUBSCRIPTIONS_ROUTE -> SUBSCRIPTIONS
            else -> null
        }

    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    @OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
    val navigationSuiteType: NavigationSuiteType = when (windowSizeClass.widthSizeClass) {
        Expanded -> NavigationSuiteType.NavigationDrawer
        Medium -> NavigationSuiteType.NavigationRail
        else -> NavigationSuiteType.NavigationBar
    }

    val currentTimeZone = timeZoneMonitor.currentTimeZone
        .stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5_000),
            TimeZone.currentSystemDefault(),
        )

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        when (topLevelDestination) {
            HOME -> navController.navigateToHomeGraph(topLevelNavOptions)
            CATEGORIES -> navController.navigateToCategories(topLevelNavOptions)
            SUBSCRIPTIONS -> navController.navigateToSubscriptions(topLevelNavOptions)
        }
    }
}