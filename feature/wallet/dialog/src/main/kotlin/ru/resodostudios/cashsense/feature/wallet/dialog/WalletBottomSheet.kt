package ru.resodostudios.cashsense.feature.wallet.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsModalBottomSheet
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.ui.R
import ru.resodostudios.cashsense.core.ui.formatAmountWithCurrency
import java.math.BigDecimal

@Composable
fun WalletBottomSheet(
    onDismiss: () -> Unit,
    onEdit: (String) -> Unit,
    viewModel: WalletDialogViewModel = hiltViewModel(),
) {
    val walletDialogState by viewModel.walletDialogUiState.collectAsStateWithLifecycle()

    WalletBottomSheet(
        walletDialogState = walletDialogState,
        onWalletEvent = viewModel::onWalletDialogEvent,
        onDismiss = onDismiss,
        onEdit = onEdit,
    )
}

@Composable
fun WalletBottomSheet(
    walletDialogState: WalletDialogUiState,
    onWalletEvent: (WalletDialogEvent) -> Unit,
    onDismiss: () -> Unit,
    onEdit: (String) -> Unit,
) {
    CsModalBottomSheet(
        onDismiss = onDismiss
    ) {
        ListItem(
            headlineContent = { Text(walletDialogState.title) },
            leadingContent = {
                Icon(
                    imageVector = ImageVector.vectorResource(CsIcons.Wallet),
                    contentDescription = null,
                )
            },
            supportingContent = {
                Text(
                    text = walletDialogState.currentBalance.ifEmpty {
                        BigDecimal(0).formatAmountWithCurrency(walletDialogState.currency)
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        )
        HorizontalDivider(Modifier.padding(16.dp))
        ListItem(
            headlineContent = { Text(stringResource(R.string.edit)) },
            leadingContent = {
                Icon(
                    imageVector = ImageVector.vectorResource(CsIcons.Edit),
                    contentDescription = null,
                )
            },
            modifier = Modifier.clickable {
                onEdit(walletDialogState.id)
                onDismiss()
            },
        )
        ListItem(
            headlineContent = { Text(stringResource(R.string.delete)) },
            leadingContent = {
                Icon(
                    imageVector = ImageVector.vectorResource(CsIcons.Delete),
                    contentDescription = null,
                )
            },
            modifier = Modifier.clickable {
                onWalletEvent(WalletDialogEvent.Delete(walletDialogState.id))
                onDismiss()
            },
        )
    }
}