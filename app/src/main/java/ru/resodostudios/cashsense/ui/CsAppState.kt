package ru.resodostudios.cashsense.ui

import androidx.compose.material3.adaptive.navigation.suite.ExperimentalMaterial3AdaptiveNavigationSuiteApi
import androidx.compose.material3.adaptive.navigation.suite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import ru.resodostudios.cashsense.feature.categories.navigation.categoriesNavigationRoute
import ru.resodostudios.cashsense.feature.categories.navigation.navigateToCategories
import ru.resodostudios.cashsense.feature.home.navigation.homeRoute
import ru.resodostudios.cashsense.feature.home.navigation.navigateToHomeGraph
import ru.resodostudios.cashsense.feature.subscriptions.navigation.navigateToSubscriptions
import ru.resodostudios.cashsense.feature.subscriptions.navigation.SUBSCRIPTIONS_ROUTE
import ru.resodostudios.cashsense.navigation.TopLevelDestination
import ru.resodostudios.cashsense.navigation.TopLevelDestination.CATEGORIES
import ru.resodostudios.cashsense.navigation.TopLevelDestination.HOME
import ru.resodostudios.cashsense.navigation.TopLevelDestination.SUBSCRIPTIONS

@Composable
fun rememberCsAppState(
    windowSize: DpSize,
    navController: NavHostController = rememberNavController()
): CsAppState {

    return remember(
        windowSize,
        navController
    ) {
        CsAppState(
            windowSize,
            navController
        )
    }
}

class CsAppState(
    private val windowSize: DpSize,
    val navController: NavHostController
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            homeRoute -> HOME
            categoriesNavigationRoute -> CATEGORIES
            SUBSCRIPTIONS_ROUTE -> SUBSCRIPTIONS
            else -> null
        }

    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    @OptIn(ExperimentalMaterial3AdaptiveNavigationSuiteApi::class)
    val navigationSuiteType: NavigationSuiteType
        @Composable get() {
            return if (windowSize.width > 1240.dp) {
                NavigationSuiteType.NavigationDrawer
            } else if (windowSize.width >= 600.dp) {
                NavigationSuiteType.NavigationRail
            } else {
                NavigationSuiteType.NavigationBar
            }
        }

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