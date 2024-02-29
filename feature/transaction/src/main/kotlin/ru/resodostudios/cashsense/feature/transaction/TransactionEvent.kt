package ru.resodostudios.cashsense.feature.transaction

import ru.resodostudios.cashsense.core.model.data.Category

sealed interface TransactionEvent {

    data class UpdateAmount(val amount: String) : TransactionEvent

    data class UpdateDescription(val description: String) : TransactionEvent

    data class UpdateCategory(val category: Category) : TransactionEvent

    data object Confirm : TransactionEvent
}