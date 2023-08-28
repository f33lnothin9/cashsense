package ru.resodostudios.cashsense.core.model.data

import java.time.LocalDateTime

data class Transaction(
    val id: Int?,
    val description: String?,
    val category: String?,
    val value: Int,
    val date: LocalDateTime
)