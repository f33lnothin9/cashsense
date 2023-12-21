package ru.resodostudios.cashsense.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource

@Composable
fun CsAlertDialog(
    @StringRes titleRes: Int,
    @StringRes confirmButtonTextRes: Int,
    @StringRes dismissButtonTextRes: Int,
    icon: ImageVector,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(icon, contentDescription = null) },
        title = { Text(text = stringResource(titleRes)) },
        text = content,
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                Text(text = stringResource(confirmButtonTextRes))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(dismissButtonTextRes))
            }
        }
    )
}