package ru.resodostudios.cashsense.feature.categories.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val categoriesNavigationRoute = "categories_route"

fun NavController.navigateToCategories(navOptions: NavOptions? = null) {
    this.navigate(categoriesNavigationRoute, navOptions)
}

fun NavGraphBuilder.categoriesScreen() {
    composable(
        route = categoriesNavigationRoute
    ) {

    }
}