package ru.resodostudios.cashsense.feature.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.CategoriesRepository
import ru.resodostudios.cashsense.core.model.data.Category
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) : ViewModel() {

    val categoriesUiState: SharedFlow<CategoriesUiState> =
        categoriesRepository.getCategories()
            .map<List<Category>, CategoriesUiState>(CategoriesUiState::Success)
            .onStart { emit(CategoriesUiState.Loading) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = CategoriesUiState.Loading,
            )

    fun upsertCategory(category: Category) {
        viewModelScope.launch {
            categoriesRepository.upsertCategory(category)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoriesRepository.deleteCategory(category)
        }
    }
}

sealed interface CategoriesUiState {

    data object Loading : CategoriesUiState

    data class Success(
        val categories: List<Category>
    ) : CategoriesUiState
}