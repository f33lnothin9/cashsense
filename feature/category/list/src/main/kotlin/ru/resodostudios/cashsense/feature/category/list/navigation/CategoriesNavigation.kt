package ru.resodostudios.cashsense.feature.category.list.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.category.list.CategoriesScreen

@Serializable
object CategoriesBaseRoute

@Serializable
object CategoriesRoute

fun NavController.navigateToCategories(navOptions: NavOptions? = null) =
    navigate(route = CategoriesRoute, navOptions)

fun NavGraphBuilder.categoriesSection(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation<CategoriesBaseRoute>(
        startDestination = CategoriesRoute,
    ) {
        composable<CategoriesRoute> {
            CategoriesScreen(onShowSnackbar)
        }
        nestedGraphs()
    }
}