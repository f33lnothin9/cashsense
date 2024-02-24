package ru.resodostudios.cashsense.feature.categories

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.ui.EditAndDeleteDropdownMenu
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.SwipeToDeleteContainer
import ru.resodostudios.cashsense.core.ui.getIconId
import ru.resodostudios.cashsense.feature.categories.CategoriesUiState.Loading
import ru.resodostudios.cashsense.feature.categories.CategoriesUiState.Success
import ru.resodostudios.cashsense.feature.category.CategoryDialog
import ru.resodostudios.cashsense.feature.category.CategoryEvent
import ru.resodostudios.cashsense.feature.category.CategoryViewModel

@Composable
internal fun CategoryRoute(
    categoriesViewModel: CategoriesViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    val categoriesState by categoriesViewModel.categoriesUiState.collectAsStateWithLifecycle()

    CategoriesScreen(
        categoriesState = categoriesState,
        onCategoryEvent = categoryViewModel::onCategoryEvent,
        onDelete = categoriesViewModel::deleteCategory
    )
}

@Composable
internal fun CategoriesScreen(
    categoriesState: CategoriesUiState,
    onCategoryEvent: (CategoryEvent) -> Unit,
    onDelete: (Category) -> Unit
) {
    var showCategoryDialog by rememberSaveable { mutableStateOf(false) }

    when (categoriesState) {
        Loading -> LoadingState()
        is Success -> if (categoriesState.categories.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(300.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                categories(
                    categoriesState = categoriesState,
                    onEdit = {
                        onCategoryEvent(CategoryEvent.UpdateId(it))
                        showCategoryDialog = true
                    },
                    onDelete = onDelete,
                )
            }
            if (showCategoryDialog) {
                CategoryDialog(
                    onDismiss = { showCategoryDialog = false }
                )
            }
        } else {
            EmptyState(
                messageRes = R.string.feature_categories_categories_empty,
                animationRes = R.raw.anim_empty_categories
            )
        }
    }
}

private fun LazyGridScope.categories(
    categoriesState: CategoriesUiState,
    onEdit: (String) -> Unit,
    onDelete: (Category) -> Unit,
) {
    when (categoriesState) {
        Loading -> Unit
        is Success -> {
            items(
                items = categoriesState.categories,
                key = { it.id!! },
                contentType = { "category" },
            ) { category ->
                SwipeToDeleteContainer(
                    item = category,
                    onDelete = { onDelete(category) },
                ) {
                    val context = LocalContext.current

                    ListItem(
                        headlineContent = {
                            Text(category.title.toString())
                        },
                        trailingContent = {
                            EditAndDeleteDropdownMenu(
                                onEdit = { onEdit(category.id.toString()) },
                                onDelete = { onDelete(category) },
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = ImageVector.vectorResource(category.icon?.getIconId(context) ?: CsIcons.Category),
                                contentDescription = null,
                            )
                        }
                    )
                }
            }
        }
    }
}