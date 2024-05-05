package ru.resodostudios.cashsense.feature.category.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.CategoriesRepository
import ru.resodostudios.cashsense.core.model.data.Category
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository,
) : ViewModel() {

    private val shouldDisplayUndoCategoryState = MutableStateFlow(false)
    private val lastRemovedCategoryState = MutableStateFlow<Category?>(null)

    val categoriesUiState: StateFlow<CategoriesUiState> = combine(
        categoriesRepository.getCategories(),
        shouldDisplayUndoCategoryState,
        lastRemovedCategoryState,
    ) { categories, shouldDisplayUndoCategory, lastRemovedCategory ->
        val updatedCategories = lastRemovedCategory
            .let(categories::minus)
            .filterIsInstance<Category>()
        CategoriesUiState.Success(
            shouldDisplayUndoCategory,
            updatedCategories,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CategoriesUiState.Loading,
        )

    fun hideCategory(category: Category) {
        shouldDisplayUndoCategoryState.value = true
        lastRemovedCategoryState.value = category
    }

    fun undoCategoryRemoval() {
        lastRemovedCategoryState.value = null
        clearUndoState()
    }

    fun clearUndoState() {
        lastRemovedCategoryState.value?.let {
            viewModelScope.launch {
                categoriesRepository.deleteCategory(it)
            }
        }
        shouldDisplayUndoCategoryState.value = false
    }
}

sealed interface CategoriesUiState {

    data object Loading : CategoriesUiState

    data class Success(
        val shouldDisplayUndoCategory: Boolean,
        val categories: List<Category>,
    ) : CategoriesUiState
}