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
import ru.resodostudios.cashsense.feature.category.dialog.CategoryDialogEvent.Save
import ru.resodostudios.cashsense.feature.category.dialog.CategoryDialogEvent.UpdateCategoryId
import ru.resodostudios.cashsense.feature.category.dialog.CategoryDialogEvent.UpdateIcon
import ru.resodostudios.cashsense.feature.category.dialog.CategoryDialogEvent.UpdateTitle
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
            Save -> saveCategory()
            is UpdateCategoryId -> updateCategoryId(event.id)
            is UpdateTitle -> updateTitle(event.title)
            is UpdateIcon -> updateIcon(event.iconId)
        }
    }

    private fun saveCategory() {
        val category = Category(
            id = _categoryDialogUiState.value.id.ifEmpty { UUID.randomUUID().toString() },
            title = _categoryDialogUiState.value.title,
            iconId = _categoryDialogUiState.value.iconId,
        )
        viewModelScope.launch {
            categoriesRepository.upsertCategory(category)
        }
        _categoryDialogUiState.update {
            CategoryDialogUiState()
        }
    }

    private fun updateCategoryId(id: String) {
        _categoryDialogUiState.update {
            it.copy(id = id)
        }
        loadCategory()
    }

    private fun updateTitle(title: String) {
        _categoryDialogUiState.update {
            it.copy(title = title)
        }
    }

    private fun updateIcon(iconId: Int) {
        _categoryDialogUiState.update {
            it.copy(iconId = iconId)
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
                        iconId = it.iconId ?: 0,
                    )
                }
        }
    }
}

data class CategoryDialogUiState(
    val id: String = "",
    val title: String = "",
    val iconId: Int = 0,
    val isLoading: Boolean = false,
)