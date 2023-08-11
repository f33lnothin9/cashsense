package ru.resodostudios.cashsense.core.database.model

import android.graphics.drawable.Icon
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.resodostudios.cashsense.core.model.data.Category

@Entity(
    tableName = "categories",
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    @Embedded
    val icon: Icon
)

fun CategoryEntity.asExternalModel() = Category(
    id = id,
    title = title,
    icon = icon
)
