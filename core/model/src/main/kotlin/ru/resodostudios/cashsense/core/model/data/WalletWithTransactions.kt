package ru.resodostudios.cashsense.core.model.data

data class WalletWithTransactions(
    val wallet: Wallet,
    val transactions: List<Transaction>
)