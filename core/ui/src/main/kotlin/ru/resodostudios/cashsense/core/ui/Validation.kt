package ru.resodostudios.cashsense.core.ui

fun String.validateAmount(): Pair<String, Boolean> {
    var cleanedInput = this.replace(",", ".")
    cleanedInput = cleanedInput.replace("-", "")
    val parts = cleanedInput.split(".")

    cleanedInput = if (parts.size > 1) {
        val decimalPart = parts[1].substring(0, minOf(parts[1].length, 2))
        "${parts[0]}.$decimalPart"
    } else {
        cleanedInput
    }

    if (cleanedInput.toDoubleOrNull() == 0.0) {
        return cleanedInput to false
    }

    val regex = Regex("^\\d+(\\.\\d{1,2})?$")
    val isValid = regex.matches(cleanedInput)

    return cleanedInput to isValid
}