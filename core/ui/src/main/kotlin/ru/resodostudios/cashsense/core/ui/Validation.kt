package ru.resodostudios.cashsense.core.ui

fun String.cleanAndValidateAmount(): Pair<String, Boolean> {
    if (isEmpty()) return "" to false // Handle empty strings

    val regex = Regex("^\\d+(\\.\\d{0,2})?") // Match integers and decimals up to 2 digits
    val matchResult = regex.find(this.replace(",", ".")) // Directly match and replace comma

    val cleanedInput = matchResult?.value ?: ""
    return cleanedInput to (cleanedInput.isNotEmpty())
}