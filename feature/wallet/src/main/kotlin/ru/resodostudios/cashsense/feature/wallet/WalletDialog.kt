package ru.resodostudios.cashsense.feature.wallet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
        dismissButtonTextRes = uiR.string.cancel,
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
        onDismiss = onDismiss
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
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
            }
            item {
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
            }
            item {
                CurrencyExposedDropdownMenuBox(
                    currencyName = currency.name,
                    onCurrencyClick = { currency = it }
                )
            }
        }
    }
    LaunchedEffect(Unit) {
        titleTextField.requestFocus()
    }
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
        dismissButtonTextRes = uiR.string.cancel,
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
        onDismiss = onDismiss
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
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
            }
            item {
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
            }
            item {
                CurrencyExposedDropdownMenuBox(
                    currencyName = currency.name,
                    onCurrencyClick = { currency = it }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyExposedDropdownMenuBox(
    currencyName: String,
    onCurrencyClick: (Currency) -> Unit
) {
    val currencyList = Currency.entries
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = currencyName,
            onValueChange = {},
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
            label = { Text(text = stringResource(R.string.currency)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            currencyList.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption.name) },
                    onClick = {
                        onCurrencyClick(selectionOption)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}