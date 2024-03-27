package ru.resodostudios.cashsense.feature.category.dialog

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
class CategoryDialogViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) : ViewModel() {

    private val _categoryUiState = MutableStateFlow(CategoryUiState())
    val categoryUiState = _categoryUiState.asStateFlow()

    fun onCategoryEvent(event: CategoryDialogEvent) {
        when (event) {
            CategoryDialogEvent.Save -> {
                val category = Category(
                    id = _categoryUiState.value.id.ifEmpty { UUID.randomUUID().toString() },
                    title = _categoryUiState.value.title,
                    iconId = _categoryUiState.value.icon
                )
                viewModelScope.launch {
                    categoriesRepository.upsertCategory(category)
                }
                _categoryUiState.update {
                    it.copy(
                        id = "",
                        title = "",
                        icon = 0,
                        isEditing = false,
                    )
                }
            }

            CategoryDialogEvent.Delete -> {
                viewModelScope.launch {
                    categoriesRepository.deleteCategory(_categoryUiState.value.id)
                }
            }

            is CategoryDialogEvent.UpdateId -> {
                _categoryUiState.update {
                    it.copy(id = event.id)
                }
                loadCategory()
            }

            is CategoryDialogEvent.UpdateTitle -> {
                _categoryUiState.update {
                    it.copy(title = event.title)
                }
            }

            is CategoryDialogEvent.UpdateIcon -> {
                _categoryUiState.update {
                    it.copy(icon = event.icon)
                }
            }
        }
    }

    private fun loadCategory() {
        viewModelScope.launch {
            categoriesRepository.getCategory(_categoryUiState.value.id)
                .onStart { _categoryUiState.value = CategoryUiState(isEditing = true) }
                .catch { _categoryUiState.value = CategoryUiState() }
                .collect {
                    _categoryUiState.value = CategoryUiState(
                        id = it.id.toString(),
                        title = it.title.toString(),
                        icon = it.iconId ?: 0,
                        isEditing = true,
                    )
                }
        }
    }
}

data class CategoryUiState(
    val id: String = "",
    val title: String = "",
    val icon: Int = 0,
    val isEditing: Boolean = false,
)