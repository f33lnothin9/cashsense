package ru.resodostudios.cashsense.core.database.model

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
    val icon: String,
)

fun CategoryEntity.asExternalModel() = Category(
    id = id,
    title = title,
    icon = icon,
)