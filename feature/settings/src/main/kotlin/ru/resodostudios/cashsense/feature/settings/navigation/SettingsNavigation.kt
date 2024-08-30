package ru.resodostudios.cashsense.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.settings.SettingsScreen

@Serializable
data object SettingsGraph

@Serializable
data object SettingsRoute

fun NavController.navigateToSettingsGraph(navOptions: NavOptions? = null) =
    navigate(route = SettingsGraph, navOptions)

fun NavGraphBuilder.settingsGraph(
    onLicensesClick: () -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation<SettingsGraph>(
        startDestination = SettingsRoute,
    ) {
        composable<SettingsRoute> {
            SettingsScreen(onLicensesClick)
        }
        nestedGraphs()
    }
}