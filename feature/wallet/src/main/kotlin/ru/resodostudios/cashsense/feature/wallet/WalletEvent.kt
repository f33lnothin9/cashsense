package ru.resodostudios.cashsense.feature.wallet

import androidx.compose.ui.text.input.TextFieldValue

sealed interface WalletEvent {

    data class UpdateId(val id: String) : WalletEvent

    data class UpdateTitle(val title: TextFieldValue) : WalletEvent

    data class UpdateInitialBalance(val initialBalance: TextFieldValue) : WalletEvent

    data class UpdateCurrency(val currency: String) : WalletEvent

    data object Confirm : WalletEvent
}