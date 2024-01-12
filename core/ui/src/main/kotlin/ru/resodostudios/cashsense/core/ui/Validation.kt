package ru.resodostudios.cashsense.core.ui

fun validateAmount(input: String): Pair<String, Boolean> {
    var cleanedInput = input.replace(",", ".")
    val parts = cleanedInput.split(".")

    val validInput = if (parts.size > 1) {
        val decimalPart = parts[1].substring(0, minOf(parts[1].length, 2))
        "${parts[0]}.$decimalPart"
    } else {
        cleanedInput
    }

    cleanedInput = validInput.replace("^--".toRegex(), "-")

    val regex = Regex("^-?\\d+(\\.\\d{1,2})?$")
    val isValid = regex.matches(cleanedInput)

    return Pair(cleanedInput, isValid)
}