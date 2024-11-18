package ru.resodostudios.cashsense.feature.subscription.dialog

import kotlinx.datetime.Instant
import java.util.Currency

sealed interface SubscriptionDialogEvent {

    data class UpdateId(val id: String) : SubscriptionDialogEvent

    data class UpdateTitle(val title: String) : SubscriptionDialogEvent

    data class UpdateAmount(val amount: String) : SubscriptionDialogEvent

    data class UpdatePaymentDate(val paymentDate: Instant) : SubscriptionDialogEvent

    data class UpdateCurrency(val currency: Currency) : SubscriptionDialogEvent

    data class UpdateReminderSwitch(val isReminderActive: Boolean) : SubscriptionDialogEvent

    data class UpdateRepeatingInterval(val repeatingInterval: RepeatingIntervalType) : SubscriptionDialogEvent

    data object Save : SubscriptionDialogEvent
}