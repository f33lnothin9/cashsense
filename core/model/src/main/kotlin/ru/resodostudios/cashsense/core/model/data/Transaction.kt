package ru.resodostudios.cashsense.core.model.data

import android.os.Parcelable
import kotlinx.datetime.Instant
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.math.BigDecimal

@Parcelize
data class Transaction(
    val id: String,
    val walletOwnerId: String,
    val categoryId: String? = null,
    val description: String?,
    val amount: BigDecimal,
    val date: @RawValue Instant
) : Parcelable