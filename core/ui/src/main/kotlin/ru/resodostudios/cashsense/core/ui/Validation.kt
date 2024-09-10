package ru.resodostudios.cashsense.core.ui

/**
 * Cleans and validates a string representing a monetary amount.
 *
 * This function removes any non-digit characters except for a single dot (.) as a decimal separator.
 * It also ensures that there are at most two decimal places. Commas (,) are automatically replaced with dots.
 *
 * @return A Pair where the first element is the cleaned amount string,
 *         and the second element is a boolean indicating if the input was valid (true if cleaned string is not empty).
 */
fun String.cleanAndValidateAmount(): Pair<String, Boolean> {
    if (isEmpty()) return "" to false // Handle empty strings

    val regex = Regex("^\\d+(\\.\\d{0,2})?") // Match integers and decimals up to 2 digits
    val matchResult = regex.find(this.replace(",", ".")) // Directly match and replace comma

    val cleanedInput = matchResult?.value ?: ""
    return cleanedInput to (cleanedInput.isNotEmpty())
}