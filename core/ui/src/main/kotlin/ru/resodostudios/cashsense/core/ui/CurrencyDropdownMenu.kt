package ru.resodostudios.cashsense.core.ui

import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import java.util.Currency
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDropdownMenu(
    currencyName: String,
    onCurrencyClick: (Currency) -> Unit,
    dropDownHeight: Dp = 200.dp,
    modifier: Modifier = Modifier,
) {
    var filterText by remember { mutableStateOf(currencyName) }

    LaunchedEffect(currencyName) {
        if (currencyName.isNotEmpty()) {
            filterText = currencyName
        }
    }
    val currencies = Currency.getAvailableCurrencies()
        .filterNot { it.displayName.contains("""\d+""".toRegex()) }
        .sortedBy { it.currencyCode }
    var selectedCurrency by remember { mutableStateOf(currencies.find { it.currencyCode == currencyName }) }
    var expanded by remember { mutableStateOf(false) }
    val filteredCurrencies = currencies.filter {
        it.currencyCode.contains(filterText, ignoreCase = true) ||
                it.displayName.contains(filterText, ignoreCase = true)
    }
    val focusManager = LocalFocusManager.current

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            modifier = modifier.menuAnchor(MenuAnchorType.PrimaryEditable),
            readOnly = false,
            value = filterText,
            singleLine = true,
            onValueChange = { newText ->
                filterText = newText
                expanded = true
            },
            label = { Text(stringResource(localesR.string.currency)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                selectedCurrency?.let {
                    onCurrencyClick(it)
                    filterText = it.currencyCode
                    focusManager.clearFocus()
                }
            },
            modifier = Modifier.heightIn(max = dropDownHeight)
        ) {
            filteredCurrencies.forEach { selectionOption ->
                val currencyText = "${selectionOption.currencyCode} - ${selectionOption.displayName}"
                DropdownMenuItem(
                    text = { Text(currencyText) },
                    onClick = {
                        selectedCurrency = selectionOption
                        onCurrencyClick(selectionOption)
                        filterText = selectionOption.currencyCode
                        expanded = false
                        focusManager.clearFocus()
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
