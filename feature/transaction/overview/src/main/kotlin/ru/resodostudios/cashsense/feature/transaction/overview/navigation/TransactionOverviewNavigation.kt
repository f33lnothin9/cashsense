package ru.resodostudios.cashsense.feature.transaction.overview.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.transaction.overview.TransactionOverviewScreen

@Serializable
object TransactionOverviewRoute

fun NavController.navigateToTransactionOverview(
    navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(route = TransactionOverviewRoute) {
        navOptions()
    }
}

fun NavGraphBuilder.transactionOverviewScreen(
    showNavigationIcon: Boolean,
    onBackClick: () -> Unit,
) {
    composable<TransactionOverviewRoute> {
        TransactionOverviewScreen(
            showNavigationIcon = showNavigationIcon,
            onBackClick = onBackClick,
        )
    }
}