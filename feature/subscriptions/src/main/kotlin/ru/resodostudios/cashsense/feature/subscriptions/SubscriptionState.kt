package ru.resodostudios.cashsense.feature.subscriptions

data class SubscriptionState(
    val title: String = "",
    val amount: String = "",
    val paymentDate: String = "",
    val currency: String = "USD"
)