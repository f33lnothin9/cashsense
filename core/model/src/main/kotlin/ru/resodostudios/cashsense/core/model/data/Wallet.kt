package ru.resodostudios.cashsense.core.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class Wallet(
    val id: String,
    val title: String,
    val initialBalance: BigDecimal,
    val currency: String
) : Parcelable