package ru.resodostudios.cashsense.feature.wallet

data class WalletDialogUiState(
    val id: String = "",
    val title: String = "",
    val initialBalance: String = "",
    val currency: String = "USD",
    val isEditing: Boolean = false,
)