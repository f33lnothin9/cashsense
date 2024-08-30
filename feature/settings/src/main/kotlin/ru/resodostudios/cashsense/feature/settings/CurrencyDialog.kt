package ru.resodostudios.cashsense.feature.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import java.util.Currency
import ru.resodostudios.cashsense.core.ui.R as uiR

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
                text = stringResource(uiR.string.core_ui_currency),
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
                Text(stringResource(uiR.string.core_ui_ok))
            }
        },
        dismissButton = {
            TextButton(onDismiss) {
                Text(stringResource(uiR.string.core_ui_cancel))
            }
        },
        modifier = modifier,
        text = {
            val currencies = Currency.getAvailableCurrencies()
                .filterNot { it.displayName.contains("""\d+""".toRegex()) }
                .sortedBy { it.currencyCode }
            LazyColumn(Modifier.selectableGroup()) {
                currencies.forEach { currency ->
                    item {
                        val selected = currencyCodeState == currency.currencyCode
                        Box(Modifier.clip(RoundedCornerShape(18.dp))) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .selectable(
                                        selected = selected,
                                        onClick = { currencyCodeState = currency.currencyCode },
                                        role = Role.RadioButton,
                                    )
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(
                                    selected = selected,
                                    onClick = null,
                                )
                                Text(
                                    text = "${currency.currencyCode} - ${currency.displayName}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 16.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}