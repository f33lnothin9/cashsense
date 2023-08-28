package ru.resodostudios.cashsense.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import ru.resodostudios.cashsense.feature.categories.navigation.categoriesScreen
import ru.resodostudios.cashsense.feature.home.navigation.homeNavigationRoute
import ru.resodostudios.cashsense.feature.home.navigation.homeScreen
import ru.resodostudios.cashsense.feature.transactions.navigation.transactionsScreen
import ru.resodostudios.cashsense.ui.CsAppState

@Composable
fun CsNavHost(
    appState: CsAppState,
    modifier: Modifier = Modifier,
    startDestination: String = homeNavigationRoute,
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        homeScreen()
        transactionsScreen()
        categoriesScreen()
    }
}