package ru.resodostudios.cashsense.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import ru.resodostudios.cashsense.feature.categories.navigation.categoriesNavigationRoute
import ru.resodostudios.cashsense.feature.categories.navigation.navigateToCategories
import ru.resodostudios.cashsense.feature.home.navigation.homeNavigationRoute
import ru.resodostudios.cashsense.feature.home.navigation.navigateToHome
import ru.resodostudios.cashsense.feature.transactions.navigation.navigateToTransactions
import ru.resodostudios.cashsense.feature.transactions.navigation.transactionsNavigationRoute
import ru.resodostudios.cashsense.navigation.TopLevelDestination
import ru.resodostudios.cashsense.navigation.TopLevelDestination.CATEGORIES
import ru.resodostudios.cashsense.navigation.TopLevelDestination.HOME
import ru.resodostudios.cashsense.navigation.TopLevelDestination.TRANSACTIONS

@Composable
fun rememberCsAppState(
    navController: NavHostController = rememberNavController()
): CsAppState {

    return remember(
        navController
    ) {
        CsAppState(
            navController
        )
    }
}

class CsAppState(
    val navController: NavHostController
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            homeNavigationRoute -> HOME
            transactionsNavigationRoute -> TRANSACTIONS
            categoriesNavigationRoute -> CATEGORIES
            else -> null
        }

    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.values().asList()

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        when (topLevelDestination) {
            HOME -> navController.navigateToHome(topLevelNavOptions)
            TRANSACTIONS -> navController.navigateToTransactions(topLevelNavOptions)
            CATEGORIES -> navController.navigateToCategories(topLevelNavOptions)
        }
    }
}