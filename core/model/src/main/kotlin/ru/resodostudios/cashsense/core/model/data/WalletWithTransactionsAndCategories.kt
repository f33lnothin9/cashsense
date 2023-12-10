package ru.resodostudios.cashsense.core.model.data

data class WalletWithTransactionsAndCategories(
    val wallet: Wallet,
    val transactionsWithCategories: List<TransactionWithCategory>
)