package ru.resodostudios.cashsense.core.ui

sealed interface WalletDialogUiState {

    data object Loading : WalletDialogUiState

    data class Success(
        val id: String,
        val title: String,
        val initialBalance: String,
        val currentPrimaryWalletId: String,
        val currency: String,
        val isPrimary: Boolean,
    ) : WalletDialogUiState
}