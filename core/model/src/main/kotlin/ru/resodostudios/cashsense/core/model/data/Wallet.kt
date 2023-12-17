package ru.resodostudios.cashsense.core.model.data

data class Wallet(
    val walletId: Long = 0L,
    val title: String = "",
    val startBalance: Double = 0.0,
    val currency: Currency = Currency.USD
)