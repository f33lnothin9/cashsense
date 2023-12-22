package ru.resodostudios.cashsense.core.model.data

import android.os.Parcelable
import kotlinx.datetime.Instant
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.util.UUID

@Parcelize
data class Transaction(
    val transactionId: UUID,
    val walletOwnerId: Long,
    val categoryOwnerId: Long? = null,
    val description: String?,
    val amount: Double,
    val date: @RawValue Instant
) : Parcelable