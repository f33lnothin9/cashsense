package ru.resodostudios.cashsense.feature.wallet.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
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
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.component.CsModalBottomSheet
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.formatAmount
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
fun WalletMenu(
    onDismiss: () -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit,
    viewModel: WalletMenuViewModel = hiltViewModel(),
) {
    val walletMenuState by viewModel.walletMenuState.collectAsStateWithLifecycle()

    WalletMenu(
        walletMenuState = walletMenuState,
        onDismiss = onDismiss,
        onPrimaryChange = viewModel::setPrimaryWalletId,
        onEdit = onEdit,
        onDelete = onDelete,
    )
}

@Composable
fun WalletMenu(
    walletMenuState: WalletMenuUiState,
    onDismiss: () -> Unit,
    onPrimaryChange: (String, Boolean) -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit,
) {
    CsModalBottomSheet(onDismiss) {
        when (walletMenuState) {
            WalletMenuUiState.Loading -> LoadingState(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
            )

            is WalletMenuUiState.Success -> {
                Column {
                    CsListItem(
                        headlineContent = { Text(walletMenuState.userWallet.title) },
                        leadingContent = {
                            Icon(
                                imageVector = ImageVector.vectorResource(CsIcons.Wallet),
                                contentDescription = null,
                            )
                        },
                        supportingContent = {
                            Text(
                                text = walletMenuState.userWallet.currentBalance
                                    .formatAmount(walletMenuState.userWallet.currency),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                    )
                    HorizontalDivider(Modifier.padding(16.dp))
                    CsListItem(
                        headlineContent = { Text(stringResource(localesR.string.primary)) },
                        leadingContent = {
                            Icon(
                                imageVector = ImageVector.vectorResource(CsIcons.Star),
                                contentDescription = null,
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = walletMenuState.userWallet.isPrimary,
                                onCheckedChange = {
                                    onPrimaryChange(walletMenuState.userWallet.id, it)
                                },
                            )
                        }
                    )
                    CsListItem(
                        headlineContent = { Text(stringResource(localesR.string.transfer)) },
                        leadingContent = {
                            Icon(
                                imageVector = ImageVector.vectorResource(CsIcons.SendMoney),
                                contentDescription = null,
                            )
                        },
                        onClick = {
                            onDismiss()
                        },
                    )
                    CsListItem(
                        headlineContent = { Text(stringResource(localesR.string.edit)) },
                        leadingContent = {
                            Icon(
                                imageVector = ImageVector.vectorResource(CsIcons.Edit),
                                contentDescription = null,
                            )
                        },
                        onClick = {
                            onEdit(walletMenuState.userWallet.id)
                            onDismiss()
                        },
                    )
                    CsListItem(
                        headlineContent = { Text(stringResource(localesR.string.delete)) },
                        leadingContent = {
                            Icon(
                                imageVector = ImageVector.vectorResource(CsIcons.Delete),
                                contentDescription = null,
                            )
                        },
                        onClick = {
                            onDelete(walletMenuState.userWallet.id)
                            onDismiss()
                        },
                    )
                }
            }
        }
    }
}