package ru.resodostudios.cashsense.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.LunchDining
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@ExperimentalMaterial3Api
@Composable
internal fun HomeScreen() {

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            ListItem(
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.AccountBalanceWallet,
                        contentDescription = null
                    )
                },
                headlineContent = { Text(text = "1 500 ₽") },
                supportingContent = { Text(text = "Итоговый баланс") },
                trailingContent = {
                    Surface(
                        modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = "+1 500 ₽",
                            modifier = Modifier
                                .padding(
                                    start = 8.dp,
                                    top = 4.dp,
                                    end = 8.dp,
                                    bottom = 4.dp
                                ),
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            ListItem(
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.TrendingUp,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                headlineContent = { Text(text = "1 000 ₽") },
                supportingContent = { Text(text = "Доходы") },
                trailingContent = {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowRight,
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {

                    }
            )
        }

        item {
            ListItem(
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.TrendingDown,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                headlineContent = { Text(text = "-2 500 ₽") },
                supportingContent = { Text(text = "Расходы") },
                trailingContent = {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowRight,
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {

                    }
            )
        }

        item {
            Text(
                text = "Недавние транзакции",
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge
            )
        }

        item {
            ListItem(
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.LunchDining,
                        contentDescription = null
                    )
                },
                supportingContent = { Text(text = "Фастфуд") },
                headlineContent = { Text(text = "-500 ₽") },
                trailingContent = { Text(text = "11 августа") }
            )
        }

        item {
            ListItem(
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.LunchDining,
                        contentDescription = null
                    )
                },
                supportingContent = { Text(text = "Фастфуд") },
                headlineContent = { Text(text = "-500 ₽") },
                trailingContent = { Text(text = "11 августа") }
            )
        }
    }
}

@Composable
fun CsDialog(onDialog: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDialog,
        icon = { Icon(Icons.Outlined.ReceiptLong, contentDescription = null) },
        title = {
            Text(text = "Новая транзакция")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var text by rememberSaveable { mutableStateOf("") }

                TextField(
                    value = text,
                    onValueChange = { text = it },
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
                onClick = onDialog
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDialog
            ) {
                Text("Отмена")
            }
        }
    )
}