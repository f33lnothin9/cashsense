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

private val currencyFormatCache = mutableMapOf<Pair<Currency, Locale>, DecimalFormat>()

fun BigDecimal.formatAmount(
    currency: Currency,
    withPlus: Boolean = false,
    withApproximately: Boolean = false,
    locale: Locale = Locale.getDefault(),
): String {
    val formattedAmount = getDecimalFormat(currency, locale).format(this)
    return buildString {
        if (withApproximately && this@formatAmount.signum() > 0) append("≈")
        if (withPlus && this@formatAmount.signum() > 0) append("+")
        append(formattedAmount)
    }
}

fun getDecimalFormat(
    currency: Currency,
    locale: Locale = Locale.getDefault(),
) = currencyFormatCache.getOrPut(currency to locale) {
    DecimalFormat.getCurrencyInstance(locale).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 2
        this.currency = currency
    } as DecimalFormat
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