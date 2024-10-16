package ru.resodostudios.cashsense.feature.wallet.dialog

import java.math.BigDecimal

sealed interface WalletDialogEvent {

    data class UpdateId(val id: String) : WalletDialogEvent

    data class UpdateTitle(val title: String) : WalletDialogEvent

    data class UpdateInitialBalance(val initialBalance: String) : WalletDialogEvent

    data class UpdateCurrentBalance(val currentBalance: BigDecimal) : WalletDialogEvent

    data class UpdateCurrency(val currency: String) : WalletDialogEvent

    data class UpdatePrimary(val isPrimary: Boolean) : WalletDialogEvent

    data object UpdatePrimaryWalletId : WalletDialogEvent

    data object Save : WalletDialogEvent
}