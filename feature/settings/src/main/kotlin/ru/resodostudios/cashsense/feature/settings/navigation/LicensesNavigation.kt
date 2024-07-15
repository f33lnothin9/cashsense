package ru.resodostudios.cashsense.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.settings.LicensesScreen

@Serializable
data object LicensesRoute

fun NavController.navigateToLicenses() = navigate(LicensesRoute) {
    launchSingleTop = true
}

fun NavGraphBuilder.licensesRoute(
    onBackClick: () -> Unit,
) {
    composable<LicensesRoute> {
        LicensesScreen(
            onBackClick = onBackClick,
        )
    }
}