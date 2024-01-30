package ru.resodostudios.cashsense.feature.subscription

data class SubscriptionUiState(
    val title: String = "",
    val amount: String = "",
    val paymentDate: String = "",
    val currency: String = "USD",
    val isEditing: Boolean = false
)