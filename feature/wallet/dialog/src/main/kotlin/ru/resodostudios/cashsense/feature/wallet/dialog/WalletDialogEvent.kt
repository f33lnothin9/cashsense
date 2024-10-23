package ru.resodostudios.cashsense.feature.wallet.dialog

sealed interface WalletDialogEvent {

    data class UpdateId(val id: String) : WalletDialogEvent

    data class UpdateTitle(val title: String) : WalletDialogEvent

    data class UpdateInitialBalance(val initialBalance: String) : WalletDialogEvent

    data class UpdateCurrency(val currency: String) : WalletDialogEvent

    data class UpdatePrimary(val isPrimary: Boolean) : WalletDialogEvent

    data object Save : WalletDialogEvent
}