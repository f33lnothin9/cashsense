package ru.resodostudios.cashsense.feature.wallet

import androidx.compose.ui.text.input.TextFieldValue

sealed interface WalletDialogEvent {

    data class UpdateId(val id: String) : WalletDialogEvent

    data class UpdateTitle(val title: TextFieldValue) : WalletDialogEvent

    data class UpdateInitialBalance(val initialBalance: TextFieldValue) : WalletDialogEvent

    data class UpdateCurrency(val currency: String) : WalletDialogEvent

    data object Confirm : WalletDialogEvent
}