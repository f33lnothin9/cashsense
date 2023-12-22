package ru.resodostudios.cashsense.core.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionWithCategory(
    val transaction: Transaction,
    val category: Category?
) : Parcelable