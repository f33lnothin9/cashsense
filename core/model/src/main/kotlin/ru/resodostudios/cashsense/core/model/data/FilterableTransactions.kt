package ru.resodostudios.cashsense.core.model.data

data class FilterableTransactions(
    val transactions: List<TransactionWithCategory>,
    val availableCategories: List<Category>,
)
