package ru.resodostudios.cashsense.feature.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.model.data.Transaction
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionsRepository: TransactionsRepository
) : ViewModel() {

    val transactionsUiState: StateFlow<TransactionsUiState> =
        transactionsRepository.getTransactions()
            .map<List<Transaction>, TransactionsUiState>(TransactionsUiState::Success)
            .onStart { emit(TransactionsUiState.Loading) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = TransactionsUiState.Loading,
            )

    fun upsertTransactions(category: String?, description: String?, value: Int, date: String) {
        viewModelScope.launch {
            transactionsRepository.upsertTransaction(
                Transaction(
                    id = null,
                    category = category,
                    description = description,
                    value = value,
                    date = date
                )
            )
        }
    }

    fun deleteCategory(transaction: Transaction) {
        viewModelScope.launch {
            transactionsRepository.deleteTransaction(transaction)
        }
    }
}

sealed interface TransactionsUiState {

    data object Loading : TransactionsUiState

    data class Success(
        val transactions: List<Transaction>
    ) : TransactionsUiState
}