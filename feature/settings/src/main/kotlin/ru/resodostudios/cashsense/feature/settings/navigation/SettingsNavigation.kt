package ru.resodostudios.cashsense.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.settings.SettingsScreen

@Serializable
data object SettingsGraph

@Serializable
data object SettingsRoute

@Serializable
data object LicensesRoute

fun NavController.navigateToSettingsGraph(navOptions: NavOptions? = null) =
    navigate(route = SettingsGraph, navOptions)

fun NavController.navigateToLicenses() = navigate(LicensesRoute) {
    launchSingleTop = true
}

fun NavGraphBuilder.settingsGraph(
    onLicensesClick: () -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit = {},
) {
    navigation<SettingsGraph>(
        startDestination = SettingsRoute::class,
    ) {
        composable<SettingsRoute> {
            SettingsScreen(onLicensesClick)
        }
        nestedGraphs()
    }
}

fun NavGraphBuilder.licensesRoute() {
    composable<LicensesRoute> {
        LibrariesContainer()
    }
}