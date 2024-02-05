package ru.resodostudios.cashsense.feature.wallet

import ru.resodostudios.cashsense.core.model.data.Currency

data class WalletDialogUiState(
    val id: String = "",
    val title: String = "",
    val initialBalance: String = "",
    val currency: String = Currency.USD.name,
    val isEditing: Boolean = false,
)