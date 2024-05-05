package ru.resodostudios.cashsense.feature.category.dialog

sealed interface CategoryDialogEvent {

    data class UpdateId(val id: String) : CategoryDialogEvent

    data class UpdateTitle(val title: String) : CategoryDialogEvent

    data class UpdateIcon(val icon: Int) : CategoryDialogEvent

    data object Save : CategoryDialogEvent
}