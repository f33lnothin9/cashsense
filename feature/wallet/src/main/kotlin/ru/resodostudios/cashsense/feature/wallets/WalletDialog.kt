package ru.resodostudios.cashsense.feature.wallets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(CsIcons.Wallet, contentDescription = null) },
        title = { Text(text = stringResource(R.string.add_wallet)) },
        text = {
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
                    OutlinedTextField(
                        value = currency.name,
                        onValueChange = { },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        trailingIcon = {
                            CurrencyDropDownMenu { currency = it }
                        },
                        readOnly = true,
                        label = { Text(text = stringResource(R.string.currency)) },
                        maxLines = 1
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        Wallet(
                            title = title,
                            startBalance = startBalance.toDouble(),
                            currency = currency
                        )
                    )
                }
            ) {
                Text(text = stringResource(uiR.string.save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(uiR.string.cancel))
            }
        }
    )
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

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(CsIcons.Wallet, contentDescription = null) },
        title = { Text(text = stringResource(R.string.edit_wallet)) },
        text = {
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
                    OutlinedTextField(
                        value = currency.name,
                        onValueChange = { },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        trailingIcon = {
                            CurrencyDropDownMenu { currency = it }
                        },
                        readOnly = true,
                        label = { Text(text = stringResource(R.string.currency)) },
                        maxLines = 1
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        Wallet(
                            walletId = wallet.walletId,
                            title = title,
                            startBalance = startBalance.toDouble(),
                            currency = currency
                        )
                    )
                }
            ) {
                Text(text = stringResource(uiR.string.save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(uiR.string.cancel))
            }
        }
    )
}

@Composable
fun CurrencyDropDownMenu(
    onCurrencyClick: (Currency) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopStart)
    ) {
        IconButton(onClick = { expanded = true }) {
            Icon(CsIcons.DropDown, contentDescription = null)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            for (currency in Currency.entries) {
                DropdownMenuItem(
                    text = { Text(text = currency.name) },
                    onClick = {
                        onCurrencyClick(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}