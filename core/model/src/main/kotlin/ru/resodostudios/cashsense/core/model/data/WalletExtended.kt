package ru.resodostudios.cashsense.core.model.data

data class WalletExtended(
    val wallet: Wallet,
    val transactionsWithCategories: List<TransactionWithCategory>,
)