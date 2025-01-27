package ru.resodostudios.cashsense.core.model.data

data class ExtendedWallet(
    val wallet: Wallet,
    val transactionsWithCategories: List<TransactionWithCategory>,
)