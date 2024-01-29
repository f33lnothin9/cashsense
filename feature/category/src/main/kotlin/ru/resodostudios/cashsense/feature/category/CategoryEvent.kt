package ru.resodostudios.cashsense.feature.category

import androidx.annotation.DrawableRes

sealed interface CategoryEvent {
    data class UpdateTitle(val title: String) : CategoryEvent
    data class UpdateIcon(@DrawableRes val iconRes: Int) : CategoryEvent
    data object Confirm : CategoryEvent
}