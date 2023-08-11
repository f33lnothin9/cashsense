package ru.resodostudios.cashsense.core.model.data

import android.graphics.drawable.Icon
import ru.resodostudios.cashsense.core.database.model.CategoryEntity

data class Category(
    val id: Int,
    val title: String,
    val icon: Icon
)

fun Category.asEntity() = CategoryEntity(
    id = id,
    title = title,
    icon = icon
)