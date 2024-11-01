package ru.resodostudios.cashsense.core.ui

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
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
fun WalletDialog(
    walletDialogState: WalletDialogUiState,
    onDismiss: () -> Unit,
    onWalletSave: () -> Unit,
    onTitleUpdate: (String) -> Unit,
    onInitialBalanceUpdate: (String) -> Unit,
    onCurrencyUpdate: (String) -> Unit,
    onPrimaryUpdate: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (walletDialogState) {
        WalletDialogUiState.Loading -> {
            LoadingState(
                modifier = modifier
                    .fillMaxWidth()
                    .height(250.dp),
            )
        }

        is WalletDialogUiState.Success -> {
            val titleRes = if (walletDialogState.id.isBlank()) {
                localesR.string.new_wallet
            } else {
                localesR.string.edit_wallet
            }
            val confirmButtonTextRes = if (walletDialogState.id.isBlank()) {
                localesR.string.add
            } else {
                localesR.string.save
            }

            CsAlertDialog(
                titleRes = titleRes,
                confirmButtonTextRes = confirmButtonTextRes,
                dismissButtonTextRes = localesR.string.cancel,
                iconRes = CsIcons.Wallet,
                onConfirm = {
                    onWalletSave()
                    onDismiss()
                },
                isConfirmEnabled = walletDialogState.title.isNotBlank(),
                onDismiss = onDismiss,
            ) {
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
                        onValueChange = { onInitialBalanceUpdate(it.cleanAndValidateAmount().first) },
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
                        currencyCode = walletDialogState.currency,
                        onCurrencyClick = { onCurrencyUpdate(it.currencyCode) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
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
}