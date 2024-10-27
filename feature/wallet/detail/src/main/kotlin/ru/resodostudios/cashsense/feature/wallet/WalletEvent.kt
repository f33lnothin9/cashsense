package ru.resodostudios.cashsense.feature.wallet

import ru.resodostudios.cashsense.core.model.data.Category

sealed interface WalletEvent {

    data class AddToSelectedCategories(val category: Category) : WalletEvent

    data class RemoveFromSelectedCategories(val category: Category) : WalletEvent

    data class UpdateFinanceType(val financeType: FinanceType) : WalletEvent

    data class UpdateDateType(val dateType: DateType) : WalletEvent

    data object IncrementSelectedDate : WalletEvent

    data object DecrementSelectedDate : WalletEvent

    data class HideTransaction(val id: String) : WalletEvent

    data object UndoTransactionRemoval : WalletEvent

    data object ClearUndoState : WalletEvent
}