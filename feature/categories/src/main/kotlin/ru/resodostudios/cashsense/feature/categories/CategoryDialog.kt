package ru.resodostudios.cashsense.feature.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
        onDismiss = onDismiss,
        onConfirm = {
            viewModel.upsertCategory(it)
            onDismiss()
        }
    )
}

@Composable
fun CategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                painter = painterResource(CsIcons.Category),
                contentDescription = null
            )
        },
        title = { Text(text = stringResource(R.string.new_category)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    label = { Text(text = stringResource(R.string.name)) },
                    maxLines = 1
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(name)
                }
            ) {
                Text(text = stringResource(R.string.add))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}