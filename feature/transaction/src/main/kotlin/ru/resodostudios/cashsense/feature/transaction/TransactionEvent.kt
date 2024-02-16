package ru.resodostudios.cashsense.feature.transaction

import androidx.compose.ui.text.input.TextFieldValue
import ru.resodostudios.cashsense.core.model.data.Category

sealed interface TransactionEvent {

    data class UpdateAmount(val amount: TextFieldValue) : TransactionEvent

    data class UpdateDescription(val description: TextFieldValue) : TransactionEvent

    data class UpdateCategory(val category: Category) : TransactionEvent

    data object Confirm : TransactionEvent
}