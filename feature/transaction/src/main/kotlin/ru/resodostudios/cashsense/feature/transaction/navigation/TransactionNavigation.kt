package ru.resodostudios.cashsense.feature.transaction.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.resodostudios.cashsense.feature.transaction.TransactionRoute

internal const val TRANSACTION_ID_ARG = "transactionId"
internal const val WALLET_ID_ARG = "walletId"

internal const val TRANSACTION_ROUTE = "transaction_route"

internal class TransactionArgs(private val transactionId: String?, private val walletId: String) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        transactionId = savedStateHandle.get<String>(TRANSACTION_ID_ARG),
        walletId = savedStateHandle.get<String>(WALLET_ID_ARG) ?: ""
    )
}

fun NavController.navigateToTransaction(transactionId: String? = null, walletId: String) =
    navigate("$TRANSACTION_ROUTE/$transactionId/$walletId") {
        launchSingleTop = true
    }

fun NavGraphBuilder.transactionScreen(
    onBackClick: () -> Unit
) {
    composable(
        route = "$TRANSACTION_ROUTE/{$TRANSACTION_ID_ARG}/{$WALLET_ID_ARG}",
        arguments = listOf(
            navArgument(TRANSACTION_ID_ARG) {
                type = NavType.StringType
                nullable = true
            },
            navArgument(WALLET_ID_ARG) {
                type = NavType.StringType
            }
        )
    ) {
        TransactionRoute(
            onBackClick = onBackClick
        )
    }
}