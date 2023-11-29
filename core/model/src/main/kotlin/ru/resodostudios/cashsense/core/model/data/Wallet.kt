package ru.resodostudios.cashsense.core.model.data

data class Wallet(
    val id: Int = 0,
    val title: String,
    val startBalance: Float = 0f,
    val currency: Currency,
    val income: Float = 0f,
    val expenses: Float = 0f
)