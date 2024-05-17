package ru.resodostudios.cashsense.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import kotlinx.serialization.Serializable

const val WALLET_ID_KEY = "walletId"

@Serializable
data class HomeDestination(val initialWalletId: String?)

fun NavController.navigateToHome(
    initialWalletId: String? = null,
    navOptions: NavOptions? = null,
) = navigate(route = HomeDestination(initialWalletId), navOptions)