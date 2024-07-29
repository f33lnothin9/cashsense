package ru.resodostudios.cashsense.feature.wallet.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.ui.CurrencyDropdownMenu
import ru.resodostudios.cashsense.core.ui.validateAmount
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent.Save
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent.UpdateCurrency
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent.UpdateInitialBalance
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent.UpdatePrimary
import ru.resodostudios.cashsense.feature.wallet.dialog.WalletDialogEvent.UpdateTitle
import ru.resodostudios.cashsense.core.ui.R as uiR

@Composable
fun WalletDialog(
    onDismiss: () -> Unit,
    viewModel: WalletDialogViewModel = hiltViewModel(),
) {
    val walletDialogState by viewModel.walletDialogUiState.collectAsStateWithLifecycle()

    WalletDialog(
        walletDialogState = walletDialogState,
        onWalletDialogEvent = viewModel::onWalletDialogEvent,
        onDismiss = onDismiss,
    )
}

@Composable
fun WalletDialog(
    walletDialogState: WalletDialogUiState,
    onWalletDialogEvent: (WalletDialogEvent) -> Unit,
    onDismiss: () -> Unit,
) {
    val dialogTitle = if (walletDialogState.id.isNotEmpty()) R.string.feature_wallet_dialog_edit_wallet else R.string.feature_wallet_dialog_new_wallet
    val dialogConfirmText = if (walletDialogState.id.isNotEmpty()) uiR.string.core_ui_save else uiR.string.core_ui_add

    CsAlertDialog(
        titleRes = dialogTitle,
        confirmButtonTextRes = dialogConfirmText,
        dismissButtonTextRes = uiR.string.core_ui_cancel,
        iconRes = CsIcons.Wallet,
        onConfirm = {
            onWalletDialogEvent(Save)
            onDismiss()
        },
        isConfirmEnabled = walletDialogState.title.isNotBlank(),
        onDismiss = onDismiss,
    ) {
        val (titleTextField, initialBalanceTextField) = remember { FocusRequester.createRefs() }

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) {
            OutlinedTextField(
                value = walletDialogState.title,
                onValueChange = { onWalletDialogEvent(UpdateTitle(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .focusRequester(titleTextField)
                    .focusProperties { next = initialBalanceTextField },
                label = { Text(stringResource(uiR.string.core_ui_title)) },
                placeholder = { Text(stringResource(uiR.string.core_ui_title) + "*") },
                supportingText = { Text(stringResource(uiR.string.core_ui_required)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                maxLines = 1,
            )
            OutlinedTextField(
                value = walletDialogState.initialBalance,
                onValueChange = { onWalletDialogEvent(UpdateInitialBalance(it.validateAmount().first)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .focusRequester(initialBalanceTextField),
                label = { Text(stringResource(R.string.feature_wallet_dialog_initial_balance)) },
                placeholder = { Text("0") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done,
                ),
                maxLines = 1,
            )
            CurrencyDropdownMenu(
                currencyName = walletDialogState.currency,
                onCurrencyClick = { onWalletDialogEvent(UpdateCurrency(it.currencyCode)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
            )
            CsListItem(
                headlineContent = { Text(stringResource(R.string.feature_wallet_dialog_primary)) },
                leadingContent = {
                    Icon(
                        imageVector = ImageVector.vectorResource(CsIcons.Star),
                        contentDescription = null,
                    )
                },
                trailingContent = {
                    Switch(
                        checked = walletDialogState.isPrimary,
                        onCheckedChange = { onWalletDialogEvent(UpdatePrimary(it)) },
                    )
                }
            )
        }
        LaunchedEffect(Unit) {
            if (walletDialogState.id.isEmpty()) {
                titleTextField.requestFocus()
            }
        }
    }
}