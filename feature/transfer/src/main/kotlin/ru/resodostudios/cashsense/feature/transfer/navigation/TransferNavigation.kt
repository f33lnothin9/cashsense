package ru.resodostudios.cashsense.feature.transfer.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.dialog
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.transfer.TransferDialog

@Serializable
data class TransferRoute(
    val walletId: String? = null,
)

fun NavController.navigateToTransfer(
    walletId: String? = null,
    navOptions: NavOptionsBuilder.() -> Unit = {},
) = navigate(route = TransferRoute(walletId)) {
    navOptions()
}

fun NavGraphBuilder.transferDialog(
    onDismiss: () -> Unit,
) {
    dialog<TransferRoute> {
        TransferDialog(
            onDismiss = onDismiss,
        )
    }
}