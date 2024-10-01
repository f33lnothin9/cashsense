package ru.resodostudios.cashsense.core.ui

/**
 * Cleans and validates a string representing a monetary amount.
 *
 * This function removes any non-digit characters except for a single dot (.) as a decimal separator.
 * It also ensures that there are at most two decimal places. Commas (,) are automatically replaced with dots.
 * Leading and trailing whitespace is trimmed.
 *
 * @return A Pair where the first element is the cleaned amount string,
 *         and the second element is a boolean indicating if the input represents a valid non-zero amount.
 *
 * @example
 * "1,234.56".cleanAndValidateAmount() returns ("1234.56", true)
 * "  -12.345  ".cleanAndValidateAmount() returns ("12.34", true)
 * "abc".cleanAndValidateAmount() returns ("", false)
 * "0".cleanAndValidateAmount() returns ("0", false)
 */
fun String.cleanAndValidateAmount(): Pair<String, Boolean> {
    val regex = Regex("^\\d+(\\.\\d{0,2})?") // Match integers and decimals up to 2 digits
    val matchResult = regex.find(
        this
            .replace(",", ".")
            .replace("-", "")
    )

    val cleanedInput = matchResult?.value ?: ""
    if (cleanedInput.toDoubleOrNull() == 0.0) return cleanedInput to false
    return cleanedInput to (cleanedInput.isNotEmpty())
}