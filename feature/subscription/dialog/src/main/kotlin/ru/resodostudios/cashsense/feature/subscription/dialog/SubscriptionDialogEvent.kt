package ru.resodostudios.cashsense.feature.subscription.dialog

import kotlinx.datetime.Instant

sealed interface SubscriptionDialogEvent {

    data class UpdateTitle(val title: String) : SubscriptionDialogEvent

    data class UpdateAmount(val amount: String) : SubscriptionDialogEvent

    data class UpdatePaymentDate(val paymentDate: Instant) : SubscriptionDialogEvent

    data class UpdateCurrency(val currency: String) : SubscriptionDialogEvent

    data object Confirm : SubscriptionDialogEvent
}