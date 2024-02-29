package ru.resodostudios.cashsense.feature.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.CategoriesRepository
import ru.resodostudios.cashsense.core.model.data.Category
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) : ViewModel() {

    private val _categoryDialogUiState = MutableStateFlow(CategoryDialogUiState())
    val categoryDialogUiState = _categoryDialogUiState.asStateFlow()

    fun onCategoryEvent(event: CategoryEvent) {
        when (event) {
            CategoryEvent.Save -> {
                val category = Category(
                    id = _categoryDialogUiState.value.id.ifEmpty { UUID.randomUUID().toString() },
                    title = _categoryDialogUiState.value.title,
                    icon = _categoryDialogUiState.value.icon
                )
                viewModelScope.launch {
                    categoriesRepository.upsertCategory(category)
                }
                _categoryDialogUiState.update {
                    it.copy(
                        id = "",
                        title = "",
                        icon = 0,
                        isEditing = false,
                    )
                }
            }

            CategoryEvent.Delete -> {
                viewModelScope.launch {
                    categoriesRepository.deleteCategory(_categoryDialogUiState.value.id)
                }
            }

            is CategoryEvent.UpdateId -> {
                _categoryDialogUiState.update {
                    it.copy(id = event.id)
                }
                loadCategory()
            }

            is CategoryEvent.UpdateTitle -> {
                _categoryDialogUiState.update {
                    it.copy(title = event.title)
                }
            }

            is CategoryEvent.UpdateIcon -> {
                _categoryDialogUiState.update {
                    it.copy(icon = event.icon)
                }
            }
        }
    }

    private fun loadCategory() {
        viewModelScope.launch {
            categoriesRepository.getCategory(_categoryDialogUiState.value.id)
                .onStart { _categoryDialogUiState.value = CategoryDialogUiState(isEditing = true) }
                .catch { _categoryDialogUiState.value = CategoryDialogUiState() }
                .collect {
                    _categoryDialogUiState.value = CategoryDialogUiState(
                        id = it.id.toString(),
                        title = it.title.toString(),
                        icon = it.icon ?: 0,
                        isEditing = true,
                    )
                }
        }
    }
}

data class CategoryDialogUiState(
    val id: String = "",
    val title: String = "",
    val icon: Int = 0,
    val isEditing: Boolean = false,
)