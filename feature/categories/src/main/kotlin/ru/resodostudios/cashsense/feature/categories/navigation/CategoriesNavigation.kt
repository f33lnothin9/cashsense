package ru.resodostudios.cashsense.feature.categories.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import ru.resodostudios.cashsense.feature.categories.CategoryRoute

const val CATEGORIES_ROUTE = "categories_route"

fun NavController.navigateToCategories(navOptions: NavOptions? = null) = navigate(CATEGORIES_ROUTE, navOptions)

fun NavGraphBuilder.categoriesScreen() {
    composable(
        route = CATEGORIES_ROUTE
    ) {
        CategoryRoute()
    }
}