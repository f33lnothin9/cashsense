package ru.resodostudios.cashsense.feature.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

@Composable
fun CategoryDialog(
    onDismiss: () -> Unit,
    viewModel: CategoriesViewModel = hiltViewModel()
) {

    CategoryDialog(
        onDismiss = onDismiss
    )
}

@Composable
fun CategoryDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(CsIcons.Category, contentDescription = null) },
        title = {
            Text(text = "Новая категория")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var text by rememberSaveable { mutableStateOf("") }

                TextField(
                    value = text,
                    onValueChange = { text = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    label = { Text(text = "Название") },
                    maxLines = 1
                )

                Text(
                    text = "Иконка",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Отмена")
            }
        }
    )
}