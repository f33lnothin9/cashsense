package ru.resodostudios.cashsense.feature.category

import androidx.compose.ui.text.input.TextFieldValue

sealed interface CategoryEvent {

    data class UpdateId(val id: String) : CategoryEvent

    data class UpdateTitle(val title: TextFieldValue) : CategoryEvent

    data class UpdateIcon(val icon: String) : CategoryEvent

    data object Save : CategoryEvent

    data object Delete : CategoryEvent
}