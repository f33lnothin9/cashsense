package ru.resodostudios.cashsense.feature.home.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.resodostudios.cashsense.feature.home.HomeScreen

const val homeNavigationRoute = "home_route"

fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    this.navigate(homeNavigationRoute, navOptions)
}

@ExperimentalMaterial3Api
fun NavGraphBuilder.homeScreen() {
    composable(
        route = homeNavigationRoute
    ) {
        HomeScreen()
    }
}