package ru.resodostudios.cashsense.feature.subscription.detail

import kotlinx.datetime.Instant

sealed interface SubscriptionEvent {
    data class UpdateTitle(val title: String) : SubscriptionEvent
    data class UpdateAmount(val amount: String) : SubscriptionEvent
    data class UpdatePaymentDate(val paymentDate: Instant) : SubscriptionEvent
    data class UpdateCurrency(val currency: String) : SubscriptionEvent
    data object Confirm : SubscriptionEvent
}