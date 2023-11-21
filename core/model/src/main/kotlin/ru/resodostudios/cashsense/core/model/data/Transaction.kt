package ru.resodostudios.cashsense.core.model.data

import kotlinx.datetime.Instant

data class Transaction(
    val id: Int? = null,
    val description: String?,
    val category: String?,
    val value: Int,
    val date: Instant
)