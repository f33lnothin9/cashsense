package ru.resodostudios.cashsense.feature.transaction

data class TransactionUiState(
    val walletOwnerId: String = "",
    val description: String = "",
    val amount: String = "",
    val date: String = "",
    val time: String = ""
)