package ru.resodostudios.cashsense.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.SwipeDismissableNavHostState
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavHostState
import ru.resodostudios.cashsense.feature.wallets.WalletsScreen

@Composable
fun CsWearNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberSwipeDismissableNavController(),
    navHostState: SwipeDismissableNavHostState = rememberSwipeDismissableNavHostState(),
) {
    SwipeDismissableNavHost(
        navController = navController,
        state = navHostState,
        startDestination = "home",
        modifier = modifier,
    ) {
        composable(
            route = "home",
        ) {
            WalletsScreen()
        }
    }
}