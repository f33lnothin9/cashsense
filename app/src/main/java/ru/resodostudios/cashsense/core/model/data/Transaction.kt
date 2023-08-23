package ru.resodostudios.cashsense.core.model.data

data class Transaction(
    val id: Int?,
    val description: String?,
    val category: String?,
    val value: Int,
    val date: String
)