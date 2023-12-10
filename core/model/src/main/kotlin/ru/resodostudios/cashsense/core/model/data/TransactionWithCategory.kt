package ru.resodostudios.cashsense.core.model.data

data class TransactionWithCategory(
    val transaction: Transaction,
    val category: Category? = null
)