package ru.resodostudios.cashsense.core.presentation.navigation

import ru.resodostudios.cashsense.core.Constants.HOME_SCREEN

sealed class Screen(val route: String) {

    data object Home: Screen(HOME_SCREEN)
}