package ru.resodostudios.cashsense.feature.wallet.detail

import ru.resodostudios.cashsense.core.model.data.Category

sealed interface WalletEvent {

    data class AddToSelectedCategories(val category: Category) : WalletEvent

    data class RemoveFromSelectedCategories(val category: Category) : WalletEvent

    data class UpdateFinanceType(val financeType: FinanceType) : WalletEvent

    data class UpdateDateType(val dateType: DateType) : WalletEvent

    data object IncrementSelectedDate : WalletEvent

    data object DecrementSelectedDate : WalletEvent
}