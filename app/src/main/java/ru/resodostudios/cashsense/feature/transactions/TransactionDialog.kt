package ru.resodostudios.cashsense.feature.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ReceiptLong
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
import ru.resodostudios.cashsense.R
import java.time.LocalDateTime

@Composable
fun TransactionDialog(
    onDismiss: () -> Unit,
    viewModel: TransactionsViewModel = hiltViewModel()
) {

    TransactionDialog(
        onDismiss = onDismiss,
        onConfirm = {
            viewModel.upsertTransactions(
                category = "Фастфуд",
                description = "??",
                value = it.toInt(),
                date = LocalDateTime.now()
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
    var value by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Outlined.ReceiptLong, contentDescription = null) },
        title = {
            Text(text = "Новая транзакция")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                TextField(
                    value = value,
                    onValueChange = { value = it },
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
                    label = { Text(text = "Сумма транзакции") },
                    maxLines = 1
                )
                TextField(
                    value = "Фастфуд",
                    onValueChange = { },
                    trailingIcon = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(Icons.Outlined.ArrowDropDown, contentDescription = null)
                        }
                    },
                    label = { Text(text = "Категория") },
                    readOnly = true,
                    maxLines = 1
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(value) }
            ) {
                Text(stringResource(id = R.string.add))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}