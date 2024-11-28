package ru.resodostudios.cashsense.feature.category.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.CategoriesRepository
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.model.data.TransactionCategoryCrossRef
import ru.resodostudios.cashsense.core.ui.CategoriesUiState
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository,
    private val transactionsRepository: TransactionsRepository,
) : ViewModel() {

    private val shouldDisplayUndoCategoryState = MutableStateFlow(false)
    private val lastRemovedCategoryState = MutableStateFlow<Pair<Category, List<String>>?>(null)

    val categoriesUiState: StateFlow<CategoriesUiState> = combine(
        shouldDisplayUndoCategoryState,
        categoriesRepository.getCategories(),
        CategoriesUiState::Success,
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CategoriesUiState.Loading,
        )

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            val category = categoriesRepository.getCategory(id).first()
            val transactionIds = transactionsRepository.getTransactionCategoryCrossRefs(id)
                .first()
                .map { it.transactionId }
            lastRemovedCategoryState.value = Pair(category, transactionIds)
            categoriesRepository.deleteCategory(id)
            shouldDisplayUndoCategoryState.value = true
        }
    }

    fun undoCategoryRemoval() {
        viewModelScope.launch {
            lastRemovedCategoryState.value?.let {
                categoriesRepository.upsertCategory(it.first)
                it.second.forEach { transactionId ->
                    it.first.id?.let { categoryId ->
                        val crossRef = TransactionCategoryCrossRef(
                            transactionId = transactionId,
                            categoryId = categoryId,
                        )
                        transactionsRepository.upsertTransactionCategoryCrossRef(crossRef)
                    }
                }
            }
            clearUndoState()
        }
    }

    fun clearUndoState() {
        lastRemovedCategoryState.value = null
        shouldDisplayUndoCategoryState.value = false
    }
}