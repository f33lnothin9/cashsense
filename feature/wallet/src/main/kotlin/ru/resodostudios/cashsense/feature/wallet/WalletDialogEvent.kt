package ru.resodostudios.cashsense.feature.wallet

sealed interface WalletDialogEvent {

    data class UpdateId(val id: String) : WalletDialogEvent

    data class UpdateTitle(val title: String) : WalletDialogEvent

    data class UpdateInitialBalance(val initialBalance: String) : WalletDialogEvent

    data class UpdateCurrency(val currency: String) : WalletDialogEvent

    data object Confirm : WalletDialogEvent
}