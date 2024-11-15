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
import androidx.compose.runtime.saveable.rememberSaveable
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
    currencyCode: String,
    onCurrencyClick: (Currency) -> Unit,
    modifier: Modifier = Modifier,
    dropDownHeight: Dp = 200.dp,
    enabled: Boolean = true,
) {
    var selectedCurrency by rememberSaveable { mutableStateOf<Currency?>(null) }
    var currencySearchText by rememberSaveable { mutableStateOf("") }

    val currencies = Currency.getAvailableCurrencies()
        .filterNot { it.displayName.contains("""\d+""".toRegex()) }
        .sortedBy { it.currencyCode }
    val filteredCurrencies = currencies.filter {
        it.currencyCode.contains(currencySearchText, ignoreCase = true) ||
                it.displayName.contains(currencySearchText, ignoreCase = true)
    }

    val focusManager = LocalFocusManager.current
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(currencyCode) {
        currencySearchText = currencyCode
        if (Regex("^[A-Z]{3}$").matches(currencyCode) &&
            Currency.getInstance(currencyCode) in currencies
        ) {
            selectedCurrency = Currency.getInstance(currencyCode)
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            modifier = modifier.menuAnchor(MenuAnchorType.PrimaryEditable),
            value = currencySearchText,
            singleLine = true,
            onValueChange = { newText ->
                currencySearchText = newText
                expanded = true
            },
            label = { Text(stringResource(localesR.string.currency)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            enabled = enabled,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                selectedCurrency?.let {
                    onCurrencyClick(it)
                    currencySearchText = it.currencyCode
                    focusManager.clearFocus()
                }
            },
            modifier = Modifier.heightIn(max = dropDownHeight),
        ) {
            filteredCurrencies.forEach { selectionOption ->
                val currencyText = "${selectionOption.currencyCode} - ${selectionOption.displayName}"
                DropdownMenuItem(
                    text = { Text(currencyText) },
                    onClick = {
                        selectedCurrency = selectionOption
                        onCurrencyClick(selectionOption)
                        currencySearchText = selectionOption.currencyCode
                        expanded = false
                        focusManager.clearFocus()
                    },
                    leadingIcon = if (currencyCode == selectionOption.currencyCode) {
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
            if (filteredCurrencies.isEmpty()) {
                DropdownMenuItem(
                    text = { Text(stringResource(localesR.string.currency_not_found)) },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    onClick = {
                        expanded = false
                        focusManager.clearFocus()
                    },
                )
            }
        }
    }
}