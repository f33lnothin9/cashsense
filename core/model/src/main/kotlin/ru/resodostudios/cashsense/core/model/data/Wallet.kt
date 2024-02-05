package ru.resodostudios.cashsense.core.model.data

import java.math.BigDecimal

data class Wallet(
    val id: String,
    val title: String,
    val initialBalance: BigDecimal,
    val currency: String,
)