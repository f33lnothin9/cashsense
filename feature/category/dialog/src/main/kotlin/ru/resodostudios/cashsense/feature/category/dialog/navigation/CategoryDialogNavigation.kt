package ru.resodostudios.cashsense.feature.category.dialog.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.dialog
import kotlinx.serialization.Serializable
import ru.resodostudios.cashsense.feature.category.dialog.CategoryDialog

@Serializable
data class CategoryDialogRoute(
    val categoryId: String? = null,
)

fun NavController.navigateToCategoryDialog(
    categoryId: String? = null,
    navOptions: NavOptionsBuilder.() -> Unit = {},
) = navigate(route = CategoryDialogRoute(categoryId)) {
    navOptions()
}

fun NavGraphBuilder.categoryDialog(
    onDismiss: () -> Unit,
) {
    dialog<CategoryDialogRoute> {
        CategoryDialog(
            onDismiss = onDismiss,
        )
    }
}