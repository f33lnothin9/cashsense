package ru.resodostudios.cashsense.core.database.model

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.resodostudios.cashsense.core.model.data.Category

@Entity(
    tableName = "categories",
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Long?,
    val title: String?,
    @DrawableRes
    val iconRes: Int?
)

fun CategoryEntity.asExternalModel() = Category(
    categoryId = categoryId,
    title = title,
    iconRes = iconRes
)