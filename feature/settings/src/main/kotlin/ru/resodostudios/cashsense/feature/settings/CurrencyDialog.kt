package ru.resodostudios.cashsense.feature.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
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

    CsAlertDialog(
        titleRes = localesR.string.choose_currency,
        confirmButtonTextRes = localesR.string.ok,
        dismissButtonTextRes = localesR.string.cancel,
        iconRes = CsIcons.UniversalCurrencyAlt,
        onConfirm = {
            onCurrencyClick(currencyCodeState)
            onDismiss()
        },
        onDismiss = onDismiss,
        modifier = modifier,
    ) {
        CurrencyDropdownMenu(
            currencyCode = currencyCodeState,
            onCurrencyClick = { currencyCodeState = it.currencyCode },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}