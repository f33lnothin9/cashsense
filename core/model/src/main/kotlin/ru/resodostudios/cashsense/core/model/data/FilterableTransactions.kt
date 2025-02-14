package ru.resodostudios.cashsense.core.model.data

data class FilterableTransactions(
    val transactionsCategories: List<TransactionWithCategory>,
    val availableCategories: List<Category>,
)
