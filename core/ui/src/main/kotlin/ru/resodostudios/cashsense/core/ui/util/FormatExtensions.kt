package ru.resodostudios.cashsense.core.ui.util

import androidx.compose.runtime.Composable
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaZoneId
import ru.resodostudios.cashsense.core.ui.LocalTimeZone
import ru.resodostudios.cashsense.core.ui.util.FormatDateType.DATE
import ru.resodostudios.cashsense.core.ui.util.FormatDateType.DATE_TIME
import ru.resodostudios.cashsense.core.ui.util.FormatDateType.TIME
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Currency
import java.util.Locale

fun BigDecimal.formatAmount(currency: Currency, withPlus: Boolean = false): String {
    val currencyFormat = DecimalFormat.getCurrencyInstance(Locale.getDefault())
    currencyFormat.currency = currency
    val formattedAmount = currencyFormat.format(this)

    return if (withPlus && this > BigDecimal.ZERO) "+$formattedAmount" else formattedAmount
}

@Composable
fun Instant.formatDate(formatDateType: FormatDateType = DATE): String = when (formatDateType) {
    DATE_TIME -> DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
    DATE -> DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    TIME -> DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
}
    .withLocale(Locale.getDefault())
    .withZone(LocalTimeZone.current.toJavaZoneId())
    .format(toJavaInstant())

enum class FormatDateType {
    DATE_TIME,
    DATE,
    TIME,
}