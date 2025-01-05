package ru.resodostudios.cashsense.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCurrencyExchangeRate(
    @SerialName("base")
    val base: String? = null,
    @SerialName("mid")
    val exchangeRate: Double? = null,
    @SerialName("target")
    val target: String? = null,
    @SerialName("timestamp")
    val timestamp: String? = null,
)