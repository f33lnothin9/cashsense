package ru.resodostudios.cashsense.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import kotlinx.serialization.Serializable

@Serializable
data object LicensesRoute

fun NavController.navigateToLicenses() = navigate(LicensesRoute) {
    launchSingleTop = true
}

fun NavGraphBuilder.licensesRoute() {
    composable<LicensesRoute> {
        LibrariesContainer()
    }
}