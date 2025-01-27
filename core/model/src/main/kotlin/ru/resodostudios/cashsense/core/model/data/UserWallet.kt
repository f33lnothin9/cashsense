package ru.resodostudios.cashsense.core.model.data

import java.math.BigDecimal
import java.util.Currency

data class UserWallet(
    val id: String,
    val title: String,
    val initialBalance: BigDecimal,
    val currentBalance: BigDecimal,
    val currency: Currency,
    val isPrimary: Boolean,
)