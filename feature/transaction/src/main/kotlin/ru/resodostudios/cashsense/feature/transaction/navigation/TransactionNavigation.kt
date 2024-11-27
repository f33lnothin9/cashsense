package ru.resodostudios.cashsense.feature.transaction.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.dialog
import androidx.navigation.navDeepLink
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.core.util.Constants.DEEP_LINK_SCHEME_AND_HOST
import ru.resodostudios.cashsense.core.util.Constants.REPEATED_KEY
import ru.resodostudios.cashsense.core.util.Constants.TRANSACTION_ID_KEY
import ru.resodostudios.cashsense.core.util.Constants.TRANSACTION_PATH
import ru.resodostudios.cashsense.core.util.Constants.WALLET_ID_KEY
import ru.resodostudios.cashsense.feature.transaction.TransactionDialog

private const val DEEP_LINK_URI_PATTERN =
    "$DEEP_LINK_SCHEME_AND_HOST/$TRANSACTION_PATH/{$WALLET_ID_KEY}/{$TRANSACTION_ID_KEY}/{$REPEATED_KEY}"

@Serializable
data class TransactionRoute(
    val walletId: String,
    val transactionId: String? = null,
    val repeated: Boolean = false,
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
    dialog<TransactionRoute>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = DEEP_LINK_URI_PATTERN
            },
        ),
    ) {
        TransactionDialog(
            onDismiss = onDismiss,
        )
    }
}