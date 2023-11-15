package ru.resodostudios.cashsense.feature.transactions.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.resodostudios.cashsense.feature.transactions.TransactionsRoute

const val transactionsNavigationRoute = "transactions_route"

fun NavController.navigateToTransactions(navOptions: NavOptions? = null) {
    this.navigate(transactionsNavigationRoute, navOptions)
}

fun NavGraphBuilder.transactionsScreen() {
    composable(
        route = transactionsNavigationRoute
    ) {
        TransactionsRoute()
    }
}