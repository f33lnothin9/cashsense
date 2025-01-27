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
    shouldShowTopBar: Boolean,
    onBackClick: () -> Unit,
    onTransactionClick: (walletId: String, transactionId: String?, repeated: Boolean) -> Unit,
) {
    composable<TransactionOverviewRoute> {
        TransactionOverviewScreen(
            shouldShowTopBar = shouldShowTopBar,
            onBackClick = onBackClick,
            onTransactionClick = onTransactionClick,
        )
    }
}