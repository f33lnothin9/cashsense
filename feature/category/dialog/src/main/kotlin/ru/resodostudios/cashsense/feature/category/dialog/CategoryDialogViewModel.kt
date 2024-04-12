package ru.resodostudios.cashsense.feature.category.dialog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.CategoriesRepository
import ru.resodostudios.cashsense.core.model.data.Category
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CategoryDialogViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val categoryId = savedStateHandle.getStateFlow(key = CATEGORY_ID, initialValue = "")

    private val _categoryDialogUiState = MutableStateFlow(CategoryDialogUiState())
    val categoryDialogUiState = _categoryDialogUiState.asStateFlow()

    fun onCategoryEvent(event: CategoryDialogEvent) {
        when (event) {
            CategoryDialogEvent.Save -> {
                val category = Category(
                    id = categoryId.value.ifEmpty { UUID.randomUUID().toString() },
                    title = _categoryDialogUiState.value.title,
                    iconId = _categoryDialogUiState.value.icon,
                )
                viewModelScope.launch {
                    categoriesRepository.upsertCategory(category)
                }
                _categoryDialogUiState.update {
                    CategoryDialogUiState()
                }
            }

            CategoryDialogEvent.Delete -> {
                viewModelScope.launch {
                    categoriesRepository.deleteCategory(categoryId.value)
                }
            }

            is CategoryDialogEvent.UpdateId -> {
                savedStateHandle[CATEGORY_ID] = event.id
                loadCategory()
            }

            is CategoryDialogEvent.UpdateTitle -> {
                _categoryDialogUiState.update {
                    it.copy(title = event.title)
                }
            }

            is CategoryDialogEvent.UpdateIcon -> {
                _categoryDialogUiState.update {
                    it.copy(icon = event.icon)
                }
            }
        }
    }

    private fun loadCategory() {
        viewModelScope.launch {
            categoriesRepository.getCategory(categoryId.value)
                .onStart { _categoryDialogUiState.update { it.copy(isLoading = true) } }
                .onCompletion { _categoryDialogUiState.update { it.copy(isLoading = false) } }
                .catch { _categoryDialogUiState.value = CategoryDialogUiState() }
                .collect {
                    _categoryDialogUiState.value = CategoryDialogUiState(
                        title = it.title.toString(),
                        icon = it.iconId ?: 0,
                    )
                }
        }
    }
}

data class CategoryDialogUiState(
    val title: String = "",
    val icon: Int = 0,
    val isLoading: Boolean = false,
)

private const val CATEGORY_ID = "categoryId"