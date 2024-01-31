package ru.resodostudios.cashsense.core.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: String? = null,
    val title: String? = null,
    val iconRes: Int? = null
) : Parcelable