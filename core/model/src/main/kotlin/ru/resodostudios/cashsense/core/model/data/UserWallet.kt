package ru.resodostudios.cashsense.core.model.data

import java.math.BigDecimal

data class UserWallet(
    val id: String,
    val title: String,
    val initialBalance: BigDecimal,
    val currentBalance: BigDecimal,
    val currency: String,
    val isPrimary: Boolean,
)