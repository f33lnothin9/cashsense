package ru.resodostudios.cashsense.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import ru.resodostudios.cashsense.feature.category.list.navigation.categoriesScreen
import ru.resodostudios.cashsense.feature.home.navigation.HomeDestination
import ru.resodostudios.cashsense.feature.subscription.list.navigation.subscriptionsScreen
import ru.resodostudios.cashsense.ui.CsAppState
import ru.resodostudios.cashsense.ui.home2pane.homeListDetailScreen

@Composable
fun CsNavHost(
    appState: CsAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = HomeDestination(null),
        enterTransition = { slideInVertically { it / 16 } + fadeIn() },
        exitTransition = { fadeOut(tween(100)) },
        popEnterTransition = { slideInVertically { it / 16 } + fadeIn() },
        popExitTransition = { fadeOut(tween(100)) },
        modifier = modifier,
    ) {
        homeListDetailScreen(
            onShowSnackbar = onShowSnackbar,
        )
        categoriesScreen(
            onShowSnackbar = onShowSnackbar,
        )
        subscriptionsScreen(
            onShowSnackbar = onShowSnackbar,
        )
    }
}