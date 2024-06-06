package ru.resodostudios.cashsense.core.ui

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import java.util.Currency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDropdownMenu(
    currencyName: String,
    onCurrencyClick: (Currency) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currencies = Currency.getAvailableCurrencies()
        .sortedBy { it.currencyCode }
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            modifier = modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            value = currencyName,
            onValueChange = {},
            label = { Text(stringResource(R.string.core_ui_currency)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            currencies.forEach { selectionOption ->
                val currencyText = "${selectionOption.symbol} - ${selectionOption.displayName}"
                DropdownMenuItem(
                    text = { Text(currencyText) },
                    onClick = {
                        onCurrencyClick(selectionOption)
                        expanded = false
                    },
                    leadingIcon = if (currencyName == selectionOption.currencyCode) {
                        {
                            Icon(
                                imageVector = ImageVector.vectorResource(CsIcons.Check),
                                contentDescription = null,
                            )
                        }
                    } else null,
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}