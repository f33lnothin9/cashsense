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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.locales.R
import java.util.Currency

@Composable
fun CurrencyList(
    currencyCodeState:String,
    search:String = "",
    onClick:(String)->Unit
){
    val currencies = Currency.getAvailableCurrencies()
        .filterNot { it.displayName.contains("""\d+""".toRegex()) }

    val filteredCurrencies = if (search.isBlank()) {
        currencies
    } else {
        currencies.filter { currency ->
            currency.currencyCode.contains(search, ignoreCase = true) ||
                    currency.displayName.contains(search, ignoreCase = true)
        }
    }.sortedBy { it.currencyCode }

    LazyColumn(Modifier.selectableGroup()) {
        filteredCurrencies.ifEmpty {
            item {
                Text(
                    stringResource(R.string.currency_not_found),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                        .padding(16.dp)


                )
            }
        }
        filteredCurrencies.forEach { currency ->
            item {
                val selected = currencyCodeState == currency.currencyCode
                Box(Modifier.clip(RoundedCornerShape(18.dp))) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = selected,
                                onClick = { onClick(currency.currencyCode) },
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