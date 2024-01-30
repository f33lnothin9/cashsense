package ru.resodostudios.cashsense.feature.category

import androidx.annotation.DrawableRes
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

data class CategoryUiState(
    val id: String = "",
    val title: String = "",
    @DrawableRes
    val iconRes: Int = CsIcons.Category,
    val isEditing: Boolean = false
)
