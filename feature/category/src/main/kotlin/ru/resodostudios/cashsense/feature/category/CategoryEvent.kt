package ru.resodostudios.cashsense.feature.category

import androidx.annotation.DrawableRes
import androidx.compose.ui.text.input.TextFieldValue

sealed interface CategoryEvent {

    data class UpdateId(val id: String) : CategoryEvent

    data class UpdateTitle(val title: TextFieldValue) : CategoryEvent

    data class UpdateIcon(@DrawableRes val iconRes: Int) : CategoryEvent

    data object Confirm : CategoryEvent
}