package ru.resodostudios.cashsense.feature.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.ui.CurrencyDropdownMenu
import java.util.Currency
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun CurrencyDialog(
    currency: Currency,
    onCurrencyClick: (Currency) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var currencyState by rememberSaveable { mutableStateOf(currency) }

    CsAlertDialog(
        titleRes = localesR.string.choose_currency,
        confirmButtonTextRes = localesR.string.ok,
        dismissButtonTextRes = localesR.string.cancel,
        iconRes = CsIcons.UniversalCurrencyAlt,
        onConfirm = {
            onCurrencyClick(currencyState)
            onDismiss()
        },
        onDismiss = onDismiss,
        modifier = modifier,
    ) {
        CurrencyDropdownMenu(
            currency = currencyState,
            onCurrencyClick = { currencyState = it },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}