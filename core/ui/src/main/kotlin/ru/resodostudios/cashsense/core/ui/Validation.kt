package ru.resodostudios.cashsense.core.ui

/**
 * Cleans a string representing a monetary amount.
 *
 * This function removes any non-digit characters except for a single dot (.) as a decimal separator.
 * It also ensures that there are at most two decimal places. Commas (,) are automatically replaced with dots.
 * Leading and trailing whitespace is trimmed.
 */
fun String.cleanAmount(): String {
    val regex = Regex("^\\d+(\\.\\d{0,2})?") // Match integers and decimals up to 2 digits
    val matchResult = regex.find(
        this
            .replace(",", ".")
            .replace("-", "")
    )

    return matchResult?.value ?: ""
}

fun String.isAmountValid(): Boolean = this.toDoubleOrNull() != 0.0 && this.isNotBlank()