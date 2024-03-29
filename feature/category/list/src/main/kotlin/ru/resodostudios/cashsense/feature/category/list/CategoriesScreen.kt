package ru.resodostudios.cashsense.feature.category.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.StoredIcon
import ru.resodostudios.cashsense.feature.category.dialog.CategoryBottomSheet
import ru.resodostudios.cashsense.feature.category.dialog.CategoryDialog
import ru.resodostudios.cashsense.feature.category.dialog.CategoryDialogEvent
import ru.resodostudios.cashsense.feature.category.dialog.CategoryDialogViewModel
import ru.resodostudios.cashsense.feature.category.list.CategoriesUiState.Loading
import ru.resodostudios.cashsense.feature.category.list.CategoriesUiState.Success

@Composable
internal fun CategoryRoute(
    categoriesViewModel: CategoriesViewModel = hiltViewModel(),
    categoryDialogViewModel: CategoryDialogViewModel = hiltViewModel(),
) {
    val categoriesState by categoriesViewModel.categoriesUiState.collectAsStateWithLifecycle()

    CategoriesScreen(
        categoriesState = categoriesState,
        onCategoryEvent = categoryDialogViewModel::onCategoryEvent,
    )
}

@Composable
internal fun CategoriesScreen(
    categoriesState: CategoriesUiState,
    onCategoryEvent: (CategoryDialogEvent) -> Unit,
) {
    var showCategoryBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showCategoryDialog by rememberSaveable { mutableStateOf(false) }

    when (categoriesState) {
        Loading -> LoadingState()
        is Success -> if (categoriesState.categories.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(300.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                categories(
                    categoriesState = categoriesState,
                    onCategoryClick = {
                        onCategoryEvent(CategoryDialogEvent.UpdateId(it))
                        showCategoryBottomSheet = true
                    },
                )
            }
            if (showCategoryBottomSheet) {
                CategoryBottomSheet(
                    onDismiss = { showCategoryBottomSheet = false },
                    onEdit = { showCategoryDialog = true },
                )
            }
            if (showCategoryDialog) {
                CategoryDialog(
                    onDismiss = { showCategoryDialog = false },
                )
            }
        } else {
            EmptyState(
                messageRes = R.string.feature_category_list_empty,
                animationRes = R.raw.anim_categories_empty,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyGridScope.categories(
    categoriesState: CategoriesUiState,
    onCategoryClick: (String) -> Unit,
) {
    when (categoriesState) {
        Loading -> Unit
        is Success -> {
            items(
                items = categoriesState.categories,
                key = { it.id!! },
                contentType = { "category" },
            ) { category ->
                ListItem(
                    headlineContent = {
                        Text(category.title.toString())
                    },
                    leadingContent = {
                        Icon(
                            imageVector = ImageVector.vectorResource(StoredIcon.asRes(category.iconId ?: StoredIcon.CATEGORY.storedId)),
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier
                        .animateItemPlacement()
                        .clickable {
                            onCategoryClick(category.id.toString())
                        }
                )
            }
        }
    }
}