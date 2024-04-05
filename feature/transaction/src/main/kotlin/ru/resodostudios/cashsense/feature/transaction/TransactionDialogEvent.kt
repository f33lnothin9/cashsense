package ru.resodostudios.cashsense.feature.transaction

import kotlinx.datetime.Instant
import ru.resodostudios.cashsense.core.model.data.Category

sealed interface TransactionDialogEvent {

    data class UpdateId(val id: String) : TransactionDialogEvent

    data class UpdateWalletId(val id: String) : TransactionDialogEvent

    data class UpdateAmount(val amount: String) : TransactionDialogEvent

    data class UpdateFinancialType(val type: FinancialType) : TransactionDialogEvent

    data class UpdateCurrency(val currency: String) : TransactionDialogEvent

    data class UpdateDate(val date: Instant) : TransactionDialogEvent

    data class UpdateDescription(val description: String) : TransactionDialogEvent

    data class UpdateCategory(val category: Category) : TransactionDialogEvent

    data object Save : TransactionDialogEvent

    data object Delete : TransactionDialogEvent
}