package ru.resodostudios.cashsense.feature.transactions

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.datetime.Clock
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.ui.R as uiR

@Composable
fun TransactionDialog(
    onDismiss: () -> Unit,
    walletId: Long = 0,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    TransactionDialog(
        onDismiss = onDismiss,
        onConfirm = {
            viewModel.upsertTransaction(
                Transaction(
                    walletOwnerId = walletId,
                    description = "??",
                    amount = it.toDouble(),
                    date = Clock.System.now()
                )
            )
            onDismiss()
        }
    )
}

@Composable
fun TransactionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var amount by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("None") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(CsIcons.Transaction, contentDescription = null) },
        title = { Text(text = stringResource(R.string.new_transaction)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    suffix = { Text(text = "₽") },
                    textStyle = TextStyle(
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        letterSpacing = 0.5.sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    label = { Text(text = stringResource(R.string.sum)) },
                    maxLines = 1
                )
                TextField(
                    value = category,
                    onValueChange = { },
                    trailingIcon = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(CsIcons.DropDown, contentDescription = null)
                        }
                    },
                    label = { Text(text = stringResource(R.string.category)) },
                    readOnly = true,
                    maxLines = 1
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    label = { Text(text = stringResource(uiR.string.description)) },
                    maxLines = 1,
                    supportingText = { Text(text = stringResource(uiR.string.optional)) }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(amount) }
            ) {
                Text(text = stringResource(uiR.string.add))
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