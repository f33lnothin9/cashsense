package ru.resodostudios.cashsense.feature.category

sealed interface CategoryEvent {

    data class UpdateId(val id: String) : CategoryEvent

    data class UpdateTitle(val title: String) : CategoryEvent

    data class UpdateIcon(val icon: Int) : CategoryEvent

    data object Save : CategoryEvent

    data object Delete : CategoryEvent
}