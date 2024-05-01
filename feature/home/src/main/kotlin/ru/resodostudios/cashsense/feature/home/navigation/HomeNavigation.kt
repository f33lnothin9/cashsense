package ru.resodostudios.cashsense.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions

const val WALLET_ID_ARG = "walletId"
const val HOME_ROUTE = "home_route"

fun NavController.navigateToHome(walletId: String? = null, navOptions: NavOptions? = null) {
    val route = if (walletId != null) {
        "${HOME_ROUTE}?${WALLET_ID_ARG}=$walletId"
    } else {
        HOME_ROUTE
    }
    navigate(route, navOptions)
}