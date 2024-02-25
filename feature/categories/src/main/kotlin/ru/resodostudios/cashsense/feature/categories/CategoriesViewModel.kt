package ru.resodostudios.cashsense.feature.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import ru.resodostudios.cashsense.core.data.repository.CategoriesRepository
import ru.resodostudios.cashsense.core.model.data.Category
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    categoriesRepository: CategoriesRepository
) : ViewModel() {

    val categoriesUiState: StateFlow<CategoriesUiState> =
        categoriesRepository.getCategories()
            .map<List<Category>, CategoriesUiState>(CategoriesUiState::Success)
            .onStart { emit(CategoriesUiState.Loading) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = CategoriesUiState.Loading,
            )
}

sealed interface CategoriesUiState {

    data object Loading : CategoriesUiState

    data class Success(
        val categories: List<Category>
    ) : CategoriesUiState
}