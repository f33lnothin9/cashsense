package ru.resodostudios.cashsense.feature.wallets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.Currency

@Composable
fun NewWalletDialog(
    onDismiss: () -> Unit,
    walletsViewModel: WalletsViewModel = hiltViewModel()
) {

    NewWalletDialog(
        onDismiss = onDismiss,
        onConfirm = {

        }
    )
}

@Composable
fun NewWalletDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var startBalance by rememberSaveable { mutableFloatStateOf(0f) }
    var currency by rememberSaveable { mutableStateOf(Currency.USD) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(CsIcons.Wallet, contentDescription = null) },
        title = {
            Text(text = "New wallet")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    label = { Text(text = "Title") },
                    maxLines = 1
                )
                TextField(
                    value = startBalance.toString(),
                    onValueChange = { startBalance = it.toFloat() },
                    label = { Text(text = "Start balance") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    maxLines = 1
                )
                TextField(
                    value = currency.name,
                    onValueChange = { },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    trailingIcon = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(CsIcons.DropDown, contentDescription = null)
                        }
                    },
                    readOnly = true,
                    label = { Text(text = "Currency") },
                    maxLines = 1
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm() }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}