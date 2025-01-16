package ru.resodostudios.cashsense.feature.transaction

import kotlinx.datetime.Instant
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.StatusType
import java.util.Currency

sealed interface TransactionDialogEvent {

    data class UpdateTransactionId(val id: String) : TransactionDialogEvent

    data class UpdateWalletId(val id: String) : TransactionDialogEvent

    data class UpdateAmount(val amount: String) : TransactionDialogEvent

    data class UpdateTransactionType(val type: TransactionType) : TransactionDialogEvent

    data class UpdateStatus(val status: StatusType) : TransactionDialogEvent

    data class UpdateCurrency(val currency: Currency) : TransactionDialogEvent

    data class UpdateDate(val date: Instant) : TransactionDialogEvent

    data class UpdateDescription(val description: String) : TransactionDialogEvent

    data class UpdateCategory(val category: Category) : TransactionDialogEvent

    data class UpdateTransactionIgnoring(val ignored: Boolean) : TransactionDialogEvent

    data class Save(val state: TransactionDialogUiState) : TransactionDialogEvent

    data object Repeat : TransactionDialogEvent
}