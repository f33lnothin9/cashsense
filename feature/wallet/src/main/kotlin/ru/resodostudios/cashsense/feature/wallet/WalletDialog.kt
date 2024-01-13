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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.Currency
import ru.resodostudios.cashsense.core.model.data.Wallet
import ru.resodostudios.cashsense.core.ui.CurrencyExposedDropdownMenuBox
import ru.resodostudios.cashsense.core.ui.validateAmount
import ru.resodostudios.cashsense.core.ui.R as uiR

@Composable
fun AddWalletDialog(
    onDismiss: () -> Unit,
    viewModel: WalletViewModel = hiltViewModel()
) {
    AddWalletDialog(
        onDismiss = onDismiss,
        onConfirm = {
            viewModel.upsertWallet(it)
            onDismiss()
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddWalletDialog(
    onDismiss: () -> Unit,
    onConfirm: (Wallet) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var startBalance by rememberSaveable { mutableStateOf("") }
    var currency by rememberSaveable { mutableStateOf(Currency.USD) }

    val (titleTextField, amountTextField) = remember { FocusRequester.createRefs() }

    CsAlertDialog(
        titleRes = R.string.new_wallet,
        confirmButtonTextRes = uiR.string.add,
        dismissButtonTextRes = uiR.string.core_ui_cancel,
        icon = CsIcons.Wallet,
        onConfirm = {
            onConfirm(
                Wallet(
                    title = title,
                    startBalance = startBalance.toDouble(),
                    currency = currency
                )
            )
        },
        isConfirmEnabled = title.isNotBlank() && validateAmount(startBalance).second,
        onDismiss = onDismiss,
        {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    label = { Text(text = stringResource(uiR.string.title)) },
                    maxLines = 1,
                    modifier = Modifier
                        .focusRequester(titleTextField)
                        .focusProperties { next = amountTextField },
                    placeholder = { Text(text = stringResource(uiR.string.title) + "*") },
                    supportingText = { Text(text = stringResource(uiR.string.required)) }
                )
                OutlinedTextField(
                    value = startBalance,
                    onValueChange = { startBalance = it },
                    placeholder = { Text(text = "100") },
                    label = { Text(text = stringResource(R.string.start_balance)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    maxLines = 1,
                    modifier = Modifier.focusRequester(amountTextField)
                )
                CurrencyExposedDropdownMenuBox(
                    currencyName = currency.name,
                    onCurrencyClick = { currency = it }
                )
            }
            LaunchedEffect(Unit) {
                titleTextField.requestFocus()
            }
        },
    )
}

@Composable
fun EditWalletDialog(
    onDismiss: () -> Unit,
    wallet: Wallet,
    viewModel: WalletViewModel = hiltViewModel()
) {
    EditWalletDialog(
        onDismiss = onDismiss,
        onConfirm = {
            viewModel.upsertWallet(it)
            onDismiss()
        },
        wallet = wallet
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditWalletDialog(
    onDismiss: () -> Unit,
    onConfirm: (Wallet) -> Unit,
    wallet: Wallet
) {
    var title by rememberSaveable { mutableStateOf(wallet.title) }
    var startBalance by rememberSaveable { mutableStateOf(wallet.startBalance.toString()) }
    var currency by rememberSaveable { mutableStateOf(wallet.currency) }

    val (titleTextField, amountTextField) = remember { FocusRequester.createRefs() }

    CsAlertDialog(
        titleRes = R.string.edit_wallet,
        confirmButtonTextRes = uiR.string.save,
        dismissButtonTextRes = uiR.string.core_ui_cancel,
        icon = CsIcons.Wallet,
        onConfirm = {
            onConfirm(
                Wallet(
                    walletId = wallet.walletId,
                    title = title,
                    startBalance = startBalance.toDouble(),
                    currency = currency
                )
            )
        },
        isConfirmEnabled = title.isNotBlank() && validateAmount(startBalance).second,
        onDismiss = onDismiss,
        {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    label = { Text(text = stringResource(uiR.string.title)) },
                    maxLines = 1,
                    modifier = Modifier
                        .focusRequester(titleTextField)
                        .focusProperties { next = amountTextField }
                )
                OutlinedTextField(
                    value = startBalance,
                    onValueChange = { startBalance = it },
                    placeholder = { Text(text = "100") },
                    label = { Text(text = stringResource(R.string.start_balance)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    maxLines = 1,
                    modifier = Modifier.focusRequester(amountTextField)
                )
                CurrencyExposedDropdownMenuBox(
                    currencyName = currency.name,
                    onCurrencyClick = { currency = it }
                )
            }
            LaunchedEffect(Unit) {
                titleTextField.requestFocus()
            }
        },
    )
}