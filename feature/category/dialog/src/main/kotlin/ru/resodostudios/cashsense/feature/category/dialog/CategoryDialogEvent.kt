package ru.resodostudios.cashsense.feature.category.dialog

sealed interface CategoryDialogEvent {

    data class UpdateCategoryId(val id: String) : CategoryDialogEvent

    data class UpdateTitle(val title: String) : CategoryDialogEvent

    data class UpdateIcon(val iconId: Int) : CategoryDialogEvent

    data object Save : CategoryDialogEvent
}