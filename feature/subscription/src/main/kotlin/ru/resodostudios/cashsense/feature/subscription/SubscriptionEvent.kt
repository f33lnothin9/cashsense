package ru.resodostudios.cashsense.feature.subscription

import androidx.compose.ui.text.input.TextFieldValue

sealed interface SubscriptionEvent {
    data class UpdateTitle(val title: TextFieldValue) : SubscriptionEvent
    data class UpdateAmount(val amount: TextFieldValue) : SubscriptionEvent
    data class UpdatePaymentDate(val paymentDate: String) : SubscriptionEvent
    data class UpdateCurrency(val currency: String) : SubscriptionEvent
    data object Confirm : SubscriptionEvent
}