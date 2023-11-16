package ru.resodostudios.cashsense.core.ui

import java.text.SimpleDateFormat
import java.util.Locale

fun formatDate(date: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val rawDate = inputFormat.parse(date)

    return outputFormat.format(rawDate!!)
}