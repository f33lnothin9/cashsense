package ru.resodostudios.cashsense.feature.wallet

sealed interface WalletItemEvent {

    data class UpdateId(val id: String) : WalletItemEvent

    data class UpdateTitle(val title: String) : WalletItemEvent

    data class UpdateInitialBalance(val initialBalance: String) : WalletItemEvent

    data class UpdateCurrency(val currency: String) : WalletItemEvent

    data class Delete(val id: String) : WalletItemEvent

    data object Confirm : WalletItemEvent
}