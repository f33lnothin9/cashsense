package ru.resodostudios.cashsense.feature.transaction.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.dialog
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.transaction.TransactionDialog

@Serializable
data class TransactionRoute(
    val walletId: String,
    val transactionId: String?,
    val repeated: Boolean,
)

fun NavController.navigateToTransactionDialog(
    walletId: String,
    transactionId: String? = null,
    repeated: Boolean = false,
    navOptions: NavOptionsBuilder.() -> Unit = {},
) = navigate(route = TransactionRoute(walletId, transactionId, repeated)) {
    navOptions()
}

fun NavGraphBuilder.transactionDialog(
    onDismiss: () -> Unit,
) {
    dialog<TransactionRoute> {
        TransactionDialog(
            onDismiss = onDismiss,
        )
    }
}