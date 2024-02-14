package ru.resodostudios.cashsense.feature.wallet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.ui.CurrencyExposedDropdownMenuBox
import ru.resodostudios.cashsense.core.ui.validateAmount
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WalletDialog(
    walletDialogState: WalletDialogUiState,
    onWalletDialogEvent: (WalletDialogEvent) -> Unit,
    onDismiss: () -> Unit,
) {
    val dialogTitle = if (walletDialogState.isEditing) R.string.feature_wallet_edit_wallet else R.string.feature_wallet_new_wallet
    val dialogConfirmText = if (walletDialogState.isEditing) uiR.string.save else uiR.string.add

    CsAlertDialog(
        titleRes = dialogTitle,
        confirmButtonTextRes = dialogConfirmText,
        dismissButtonTextRes = uiR.string.core_ui_cancel,
        iconRes = CsIcons.Wallet,
        onConfirm = {
            onWalletDialogEvent(WalletDialogEvent.Confirm)
            onDismiss()
        },
        isConfirmEnabled = walletDialogState.title.isNotBlank() && walletDialogState.initialBalance.validateAmount().second,
        onDismiss = onDismiss,
    ) {
        val (titleTextField, initialBalanceTextField) = remember { FocusRequester.createRefs() }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = walletDialogState.title,
                onValueChange = { onWalletDialogEvent(WalletDialogEvent.UpdateTitle(it)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                label = { Text(text = stringResource(uiR.string.title)) },
                maxLines = 1,
                placeholder = { Text(text = stringResource(uiR.string.title) + "*") },
                supportingText = { Text(text = stringResource(uiR.string.required)) },
                modifier = Modifier
                    .focusRequester(titleTextField)
                    .focusProperties { next = initialBalanceTextField }
            )
            OutlinedTextField(
                value = TextFieldValue(
                    text = walletDialogState.initialBalance,
                    selection = TextRange(walletDialogState.initialBalance.length)
                ),
                onValueChange = { onWalletDialogEvent(WalletDialogEvent.UpdateInitialBalance(it.text.validateAmount().first)) },
                placeholder = { Text(text = "100") },
                label = { Text(text = stringResource(R.string.feature_wallet_initial_balance)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done,
                ),
                maxLines = 1,
                modifier = Modifier.focusRequester(initialBalanceTextField)
            )
            CurrencyExposedDropdownMenuBox(
                currencyName = walletDialogState.currency,
                onCurrencyClick = { onWalletDialogEvent(WalletDialogEvent.UpdateCurrency(it.name)) }
            )
        }
        LaunchedEffect(Unit) {
            titleTextField.requestFocus()
        }
    }
}