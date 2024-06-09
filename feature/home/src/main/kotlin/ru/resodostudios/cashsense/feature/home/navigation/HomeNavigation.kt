package ru.resodostudios.cashsense.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import kotlinx.serialization.Serializable

const val WALLET_ID_KEY = "walletId"
const val OPEN_TRANSACTION_DIALOG_KEY = "openTransactionDialog"

@Serializable
data class HomeRoute(
    val initialWalletId: String? = null,
    val openTransactionDialog: Boolean = false,
)

fun NavController.navigateToHome(
    initialWalletId: String? = null,
    openTransactionDialog: Boolean = false,
    navOptions: NavOptions? = null,
) = navigate(
    route = HomeRoute(initialWalletId, openTransactionDialog),
    navOptions = navOptions,
)