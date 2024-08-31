package ru.resodostudios.cashsense.feature.category.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.ui.CategoriesUiState
import ru.resodostudios.cashsense.core.ui.CategoriesUiState.Loading
import ru.resodostudios.cashsense.core.ui.CategoriesUiState.Success
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.StoredIcon
import ru.resodostudios.cashsense.feature.category.dialog.CategoryBottomSheet
import ru.resodostudios.cashsense.feature.category.dialog.CategoryDialog
import ru.resodostudios.cashsense.feature.category.dialog.CategoryDialogEvent
import ru.resodostudios.cashsense.feature.category.dialog.CategoryDialogEvent.UpdateCategoryId
import ru.resodostudios.cashsense.feature.category.dialog.CategoryDialogViewModel
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun CategoriesScreen(
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
    categoriesViewModel: CategoriesViewModel = hiltViewModel(),
    categoryDialogViewModel: CategoryDialogViewModel = hiltViewModel(),
) {
    val categoriesState by categoriesViewModel.categoriesUiState.collectAsStateWithLifecycle()

    CategoriesScreen(
        categoriesState = categoriesState,
        onShowSnackbar = onShowSnackbar,
        modifier = modifier,
        onCategoryEvent = categoryDialogViewModel::onCategoryEvent,
        hideCategory = categoriesViewModel::hideCategory,
        undoCategoryRemoval = categoriesViewModel::undoCategoryRemoval,
        clearUndoState = categoriesViewModel::clearUndoState,
    )
}

@Composable
internal fun CategoriesScreen(
    categoriesState: CategoriesUiState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    onCategoryEvent: (CategoryDialogEvent) -> Unit,
    modifier: Modifier = Modifier,
    hideCategory: (String) -> Unit = {},
    undoCategoryRemoval: () -> Unit = {},
    clearUndoState: () -> Unit = {},
) {
    when (categoriesState) {
        Loading -> LoadingState(modifier.fillMaxSize())
        is Success -> {
            val categoryDeletedMessage = stringResource(localesR.string.category_deleted)
            val undoText = stringResource(localesR.string.undo)

            LaunchedEffect(categoriesState.shouldDisplayUndoCategory) {
                if (categoriesState.shouldDisplayUndoCategory) {
                    val snackBarResult = onShowSnackbar(categoryDeletedMessage, undoText)
                    if (snackBarResult) {
                        undoCategoryRemoval()
                    } else {
                        clearUndoState()
                    }
                }
            }
            LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
                clearUndoState()
            }

            if (categoriesState.categories.isNotEmpty()) {
                var showCategoryBottomSheet by rememberSaveable { mutableStateOf(false) }
                var showCategoryDialog by rememberSaveable { mutableStateOf(false) }

                CategoriesGrid(
                    categoriesState = categoriesState,
                    onCategoryEvent = onCategoryEvent,
                    onShowCategoryBottomSheetChange = { showCategoryBottomSheet = true },
                    modifier = modifier,
                )
                if (showCategoryBottomSheet) {
                    CategoryBottomSheet(
                        onDismiss = { showCategoryBottomSheet = false },
                        onEdit = { showCategoryDialog = true },
                        onDelete = hideCategory,
                    )
                }
                if (showCategoryDialog) {
                    CategoryDialog(onDismiss = { showCategoryDialog = false })
                }
            } else {
                EmptyState(
                    messageRes = localesR.string.categories_empty,
                    animationRes = R.raw.anim_categories_empty,
                    modifier = modifier,
                )
            }
        }
    }
}

@Composable
private fun CategoriesGrid(
    categoriesState: CategoriesUiState,
    onCategoryEvent: (CategoryDialogEvent) -> Unit,
    onShowCategoryBottomSheetChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(300.dp),
        contentPadding = PaddingValues(
            bottom = 88.dp,
        ),
        modifier = modifier
            .fillMaxSize()
            .testTag("categories:list"),
    ) {
        categories(
            categoriesState = categoriesState,
            onCategoryClick = {
                onCategoryEvent(UpdateCategoryId(it))
                onShowCategoryBottomSheetChange()
            },
        )
    }
}

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
                CsListItem(
                    headlineContent = { Text(category.title.toString()) },
                    leadingContent = {
                        Icon(
                            imageVector = ImageVector.vectorResource(
                                StoredIcon.asRes(category.iconId ?: StoredIcon.CATEGORY.storedId)
                            ),
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.animateItem(),
                    onClick = { onCategoryClick(category.id.toString()) },
                )
            }
        }
    }
}

@Preview
@Composable
private fun CategoriesGridPreview(
    @PreviewParameter(CategoryPreviewParameterProvider::class)
    categories: List<Category>,
) {
    CsTheme {
        Surface {
            CategoriesGrid(
                categoriesState = Success(
                    shouldDisplayUndoCategory = false,
                    categories = categories,
                ),
                onCategoryEvent = {},
                onShowCategoryBottomSheetChange = {},
            )
        }
    }
}