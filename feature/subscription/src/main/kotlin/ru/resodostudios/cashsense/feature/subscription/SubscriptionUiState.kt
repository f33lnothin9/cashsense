package ru.resodostudios.cashsense.feature.subscription

import androidx.compose.ui.text.input.TextFieldValue
import ru.resodostudios.cashsense.core.model.data.Currency

data class SubscriptionUiState(
    val title: TextFieldValue = TextFieldValue(""),
    val amount: TextFieldValue = TextFieldValue(""),
    val paymentDate: String = "",
    val currency: String = Currency.USD.name,
    val isEditing: Boolean = false
)