package ru.resodostudios.cashsense.feature.transaction

import kotlinx.datetime.Clock
import ru.resodostudios.cashsense.core.model.data.Category

data class TransactionUiState(
    val walletOwnerId: String = "",
    val description: String = "",
    val amount: String = "",
    val date: String = Clock.System.now().toString(),
    val category: Category? = Category(),
    val isEditing: Boolean = false,
)