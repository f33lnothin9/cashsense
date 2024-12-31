package ru.resodostudios.cashsense.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCurrencyRate(
    @SerialName("base")
    val base: String? = null,
    @SerialName("mid")
    val mid: Double? = null,
    @SerialName("target")
    val target: String? = null,
    @SerialName("timestamp")
    val timestamp: String? = null,
)