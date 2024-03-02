package ru.resodostudios.cashsense.feature.transaction

import ru.resodostudios.cashsense.core.model.data.Category

sealed interface TransactionEvent {

    data class UpdateId(val id: String) : TransactionEvent

    data class UpdateWalletId(val id: String) : TransactionEvent

    data class UpdateAmount(val amount: String) : TransactionEvent

    data class UpdateCurrency(val currency: String) : TransactionEvent

    data class UpdateDescription(val description: String) : TransactionEvent

    data class UpdateCategory(val category: Category) : TransactionEvent

    data object Save : TransactionEvent

    data object Delete : TransactionEvent
}