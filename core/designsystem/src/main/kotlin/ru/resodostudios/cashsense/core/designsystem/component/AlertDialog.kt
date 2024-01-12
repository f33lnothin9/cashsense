package ru.resodostudios.cashsense.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign

@Composable
fun CsAlertDialog(
    @StringRes titleRes: Int,
    @StringRes confirmButtonTextRes: Int,
    @StringRes dismissButtonTextRes: Int,
    icon: ImageVector,
    onConfirm: () -> Unit,
    isConfirmEnabled: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(icon, contentDescription = null) },
        title = {
            Text(
                text = stringResource(titleRes),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = content,
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = isConfirmEnabled
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