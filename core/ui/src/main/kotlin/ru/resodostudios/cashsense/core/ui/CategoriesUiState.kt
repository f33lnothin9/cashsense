package ru.resodostudios.cashsense.core.ui

import ru.resodostudios.cashsense.core.model.data.Category

sealed interface CategoriesUiState {

    data object Loading : CategoriesUiState

    data class Success(
        val shouldDisplayUndoCategory: Boolean,
        val categories: List<Category>,
    ) : CategoriesUiState
}