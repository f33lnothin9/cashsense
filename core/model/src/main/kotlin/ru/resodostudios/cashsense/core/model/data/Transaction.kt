package ru.resodostudios.cashsense.core.model.data

import android.os.Parcelable
import kotlinx.datetime.Instant
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Transaction(
    val id: String,
    val walletOwnerId: String,
    val categoryId: String? = null,
    val description: String?,
    val amount: Double,
    val date: @RawValue Instant
) : Parcelable