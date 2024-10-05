package ru.resodostudios.cashsense.feature.settings

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.ui.CurrencyDropdownMenu
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun CurrencyDialog(
    currencyCode: String,
    onCurrencyClick: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var currencyCodeState by remember { mutableStateOf(currencyCode) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = ImageVector.vectorResource(CsIcons.UniversalCurrencyAlt),
                contentDescription = null,
            )
        },
        title = {
            Text(
                text = stringResource(localesR.string.currency),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onCurrencyClick(currencyCodeState)
                    onDismiss()
                }
            ) {
                Text(stringResource(localesR.string.ok))
            }
        },
        dismissButton = {
            TextButton(onDismiss) {
                Text(stringResource(localesR.string.cancel))
            }
        },
        modifier = modifier,
        text = {
            CurrencyDropdownMenu(
                currencyName = currencyCodeState,
                onCurrencyClick = {
                    currencyCodeState = it.currencyCode
                }
            )
        }
    )
}