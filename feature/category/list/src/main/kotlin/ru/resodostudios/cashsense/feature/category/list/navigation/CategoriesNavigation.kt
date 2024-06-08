package ru.resodostudios.cashsense.feature.category.list.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.category.list.CategoriesScreen

@Serializable
data object CategoriesRoute

fun NavController.navigateToCategories(navOptions: NavOptions) =
    navigate(route = CategoriesRoute, navOptions)

fun NavGraphBuilder.categoriesScreen(
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable<CategoriesRoute> {
        CategoriesScreen(onShowSnackbar)
    }
}