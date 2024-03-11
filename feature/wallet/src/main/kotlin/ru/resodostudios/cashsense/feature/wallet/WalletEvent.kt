package ru.resodostudios.cashsense.feature.wallet


import ru.resodostudios.cashsense.core.model.data.Category

sealed interface WalletEvent {

    data class AddToSelectedCategories(val category: Category) : WalletEvent

    data class RemoveFromSelectedCategories(val category: Category) : WalletEvent

    data class UpdateFinanceType(val financeType: FinanceType) : WalletEvent
}