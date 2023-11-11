package ru.resodostudios.cashsense.core.data.model

import ru.resodostudios.cashsense.core.database.model.CategoryEntity
import ru.resodostudios.cashsense.core.model.data.Category

fun Category.asEntity() = CategoryEntity(
    id = id,
    title = title,
    icon = icon
)