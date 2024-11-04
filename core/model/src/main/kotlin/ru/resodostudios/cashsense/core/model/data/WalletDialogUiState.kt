package ru.resodostudios.cashsense.core.model.data

data class WalletDialogUiState(
    val id: String = "",
    val title: String = "",
    val initialBalance: String = "",
    val currentPrimaryWalletId: String = "",
    val currency: String = "",
    val isPrimary: Boolean = false,
    val isLoading: Boolean = false,
    val isWalletSaved: Boolean? = null,
)