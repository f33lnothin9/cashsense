package ru.resodostudios.cashsense.feature.category.dialog

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
) : ViewModel() {

    private val _categoryDialogUiState = MutableStateFlow(CategoryDialogUiState())
    val categoryDialogUiState = _categoryDialogUiState.asStateFlow()

    fun onCategoryEvent(event: CategoryDialogEvent) {
        when (event) {
            CategoryDialogEvent.Save -> {
                val category = Category(
                    id = _categoryDialogUiState.value.id.ifEmpty { UUID.randomUUID().toString() },
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

            is CategoryDialogEvent.UpdateId -> {
                _categoryDialogUiState.update {
                    it.copy(id = event.id)
                }
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
            categoriesRepository.getCategory(_categoryDialogUiState.value.id)
                .onStart { _categoryDialogUiState.update { it.copy(isLoading = true) } }
                .onCompletion { _categoryDialogUiState.update { it.copy(isLoading = false) } }
                .catch { _categoryDialogUiState.value = CategoryDialogUiState() }
                .collect {
                    _categoryDialogUiState.value = CategoryDialogUiState(
                        id = it.id.toString(),
                        title = it.title.toString(),
                        icon = it.iconId ?: 0,
                    )
                }
        }
    }
}

data class CategoryDialogUiState(
    val id: String = "",
    val title: String = "",
    val icon: Int = 0,
    val isLoading: Boolean = false,
)