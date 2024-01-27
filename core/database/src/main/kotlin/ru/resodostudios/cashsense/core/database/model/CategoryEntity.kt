package ru.resodostudios.cashsense.core.database.model

import androidx.annotation.DrawableRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.resodostudios.cashsense.core.model.data.Category

@Entity(
    tableName = "categories",
)
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    @DrawableRes
    @ColumnInfo(name = "icon_res")
    val iconRes: Int
)

fun CategoryEntity.asExternalModel() = Category(
    id = id,
    title = title,
    iconRes = iconRes
)