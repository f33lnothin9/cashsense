package ru.resodostudios.cashsense.feature.wallet.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
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
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.ui.CurrencyDropdownMenu
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.util.cleanAmount
import java.math.BigDecimal
import java.util.Currency
import kotlin.uuid.Uuid
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun WalletDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WalletDialogViewModel = hiltViewModel(),
) {
    val walletDialogState by viewModel.walletDialogState.collectAsStateWithLifecycle()

    WalletDialog(
        walletDialogState = walletDialogState,
        onDismiss = onDismiss,
        onWalletSave = viewModel::saveWallet,
        onTitleUpdate = viewModel::updateTitle,
        onInitialBalanceUpdate = viewModel::updateInitialBalance,
        onCurrencyUpdate = viewModel::updateCurrency,
        onPrimaryUpdate = viewModel::updatePrimary,
        modifier = modifier,
    )
}

@Composable
private fun WalletDialog(
    walletDialogState: WalletDialogUiState,
    onDismiss: () -> Unit,
    onWalletSave: (Wallet, Boolean) -> Unit,
    onTitleUpdate: (String) -> Unit,
    onInitialBalanceUpdate: (String) -> Unit,
    onCurrencyUpdate: (Currency) -> Unit,
    onPrimaryUpdate: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (titleRes, confirmButtonTextRes) = if (walletDialogState.id.isNotBlank()) {
        localesR.string.edit_wallet to localesR.string.save
    } else {
        localesR.string.new_wallet to localesR.string.add
    }

    CsAlertDialog(
        titleRes = titleRes,
        confirmButtonTextRes = confirmButtonTextRes,
        dismissButtonTextRes = localesR.string.cancel,
        iconRes = CsIcons.Wallet,
        onConfirm = {
            val wallet = Wallet(
                id = walletDialogState.id.ifBlank { Uuid.random().toHexString() },
                title = walletDialogState.title,
                initialBalance = if (walletDialogState.initialBalance.isBlank()) {
                    BigDecimal.ZERO
                } else {
                    BigDecimal(walletDialogState.initialBalance)
                },
                currency = walletDialogState.currency,
            )
            onWalletSave(wallet, walletDialogState.isPrimary)
            onDismiss()
        },
        isConfirmEnabled = walletDialogState.title.isNotBlank(),
        onDismiss = onDismiss,
        modifier = modifier,
    ) {
        if (walletDialogState.isLoading) {
            LoadingState(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
            )
        } else {
            val focusManager = LocalFocusManager.current
            val focusRequester = remember { FocusRequester() }

            Column(Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = walletDialogState.title,
                    onValueChange = onTitleUpdate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .focusRequester(focusRequester),
                    label = { Text(stringResource(localesR.string.title)) },
                    placeholder = { Text(stringResource(localesR.string.title) + "*") },
                    supportingText = { Text(stringResource(localesR.string.required)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) },
                    ),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = walletDialogState.initialBalance,
                    onValueChange = { onInitialBalanceUpdate(it.cleanAmount()) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    label = { Text(stringResource(localesR.string.initial_balance)) },
                    placeholder = { Text("0") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() },
                    ),
                    singleLine = true,
                )
                CurrencyDropdownMenu(
                    currency = walletDialogState.currency,
                    onCurrencyClick = onCurrencyUpdate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    enabled = walletDialogState.isCurrencyEditable,
                )
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
                            checked = walletDialogState.isPrimary,
                            onCheckedChange = onPrimaryUpdate,
                        )
                    }
                )
            }
            LaunchedEffect(walletDialogState.id) {
                if (walletDialogState.id.isBlank()) {
                    focusRequester.requestFocus()
                }
            }
        }
    }
}