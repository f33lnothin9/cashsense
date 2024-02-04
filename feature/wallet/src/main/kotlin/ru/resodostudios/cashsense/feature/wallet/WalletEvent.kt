package ru.resodostudios.cashsense.feature.wallet

sealed interface WalletEvent {

    data class UpdateId(val id: String) : WalletEvent

    data class UpdateTitle(val title: String) : WalletEvent

    data class UpdateInitialBalance(val initialBalance: String) : WalletEvent

    data class UpdateCurrency(val currency: String) : WalletEvent

    data object Confirm : WalletEvent
}