package ru.resodostudios.cashsense.feature.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.CategoriesRepository
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.Category
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) : ViewModel() {

    private val _categoryUiState = MutableStateFlow(CategoryUiState())
    val categoryUiState = _categoryUiState.asStateFlow()

    fun onCategoryEvent(event: CategoryEvent) {
        when (event) {
            CategoryEvent.Confirm -> {
                val category = Category(
                    id = _categoryUiState.value.id.ifEmpty { UUID.randomUUID().toString() },
                    title = _categoryUiState.value.title,
                    iconRes = _categoryUiState.value.iconRes
                )
                viewModelScope.launch {
                    categoriesRepository.upsertCategory(category)
                }
                _categoryUiState.update {
                    it.copy(
                        id = "",
                        title = "",
                        iconRes = CsIcons.Category,
                        isEditing = false
                    )
                }
            }
            is CategoryEvent.UpdateId -> {
                _categoryUiState.update {
                    it.copy(id = event.id)
                }
                loadCategory()
            }
            is CategoryEvent.UpdateTitle -> {
                _categoryUiState.update {
                    it.copy(title = event.title)
                }
            }
            is CategoryEvent.UpdateIcon -> {
                _categoryUiState.update {
                    it.copy(iconRes = event.iconRes)
                }
            }
        }
    }

    private fun loadCategory() {
        viewModelScope.launch {
            categoriesRepository.getCategory(_categoryUiState.value.id)
                .onEach {
                    _categoryUiState.emit(
                        CategoryUiState(
                            isEditing = true,
                            id = it.id,
                            title = it.title.toString(),
                            iconRes = it.iconRes!!
                        )
                    )
                }
                .collect()
        }
    }
}