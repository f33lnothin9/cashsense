package ru.resodostudios.cashsense.feature.categories

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.ui.EditAndDeleteDropdownMenu
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.ui.LoadingState

@Composable
internal fun CategoryRoute(
    viewModel: CategoriesViewModel = hiltViewModel()
) {
    val categoriesState by viewModel.categoriesUiState.collectAsStateWithLifecycle()

    CategoriesScreen(
        categoriesState = categoriesState,
        onDelete = viewModel::deleteCategory
    )
}

@Composable
internal fun CategoriesScreen(
    categoriesState: CategoriesUiState,
    onDelete: (Category) -> Unit
) {
    var showEditCategoryDialog by rememberSaveable { mutableStateOf(false) }

    var categoryState by rememberSaveable { mutableStateOf(Category()) }

    when (categoriesState) {
        CategoriesUiState.Loading -> LoadingState()
        is CategoriesUiState.Success -> if (categoriesState.categories.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(300.dp),
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    categories(
                        categoriesState = categoriesState,
                        onEdit = {
                            categoryState = it
                            showEditCategoryDialog = true
                        },
                        onDelete = onDelete
                    )
                }
                if (showEditCategoryDialog) {
                    EditCategoryDialog(
                        category = categoryState,
                        onDismiss = { showEditCategoryDialog = false }
                    )
                }
            }
        } else {
            EmptyState(
                messageId = R.string.categories_empty,
                animationId = R.raw.anim_empty_categories
            )
        }
    }
}

private fun LazyGridScope.categories(
    categoriesState: CategoriesUiState,
    onEdit: (Category) -> Unit,
    onDelete: (Category) -> Unit
) {
    when (categoriesState) {
        CategoriesUiState.Loading -> Unit
        is CategoriesUiState.Success -> {
            items(categoriesState.categories) { category ->
                ListItem(
                    headlineContent = { Text(text = category.title.toString()) },
                    trailingContent = {
                        EditAndDeleteDropdownMenu(
                            onEdit = { onEdit(category) },
                            onDelete = { onDelete(category) }
                        )
                    }
                )
            }
        }
    }
}