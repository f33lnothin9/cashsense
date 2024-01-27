package ru.resodostudios.cashsense.core.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Category(
    val id: String = UUID.randomUUID().toString(),
    val title: String? = null,
    val iconRes: Int? = null
) : Parcelable