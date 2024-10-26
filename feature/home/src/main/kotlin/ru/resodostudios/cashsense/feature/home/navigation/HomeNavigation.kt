package ru.resodostudios.cashsense.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import kotlinx.serialization.Serializable

@Serializable
data class HomeRoute(
    val walletId: String? = null,
    val openTransactionDialog: Boolean = false,
    val editWalletId: String? = null,
)

fun NavController.navigateToHome(
    walletId: String? = null,
    openTransactionDialog: Boolean = false,
    navOptions: NavOptions? = null,
) = navigate(
    route = HomeRoute(walletId, openTransactionDialog),
    navOptions = navOptions,
)