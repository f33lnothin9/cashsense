package ru.resodostudios.cashsense.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import kotlinx.serialization.Serializable

@Serializable
data class HomeDestination(val walletId: String?)

fun NavController.navigateToHome(walletId: String? = null, navOptions: NavOptions? = null) =
    navigate(route = HomeDestination(walletId), navOptions)