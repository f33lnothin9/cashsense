package ru.resodostudios.cashsense.feature.subscriptions

data class SubscriptionUiState(
    val title: String = "",
    val amount: String = "",
    val paymentDate: String = "",
    val currency: String = "USD"
)