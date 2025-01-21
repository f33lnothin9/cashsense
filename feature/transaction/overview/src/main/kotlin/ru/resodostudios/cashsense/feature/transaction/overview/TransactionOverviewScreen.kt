package ru.resodostudios.cashsense.feature.transaction.overview

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.ui.component.LoadingState
import ru.resodostudios.cashsense.core.ui.transactions

@Composable
internal fun TransactionOverviewScreen(
    onBackClick: () -> Unit,
    showNavigationIcon: Boolean,
    viewModel: TransactionOverviewViewModel = hiltViewModel(),
) {
    val transactionOverviewState by viewModel.transactionOverviewUiState.collectAsStateWithLifecycle()

    TransactionOverviewScreen(
        transactionOverviewState = transactionOverviewState,
        showNavigationIcon = showNavigationIcon,
        onBackClick = onBackClick,
        updateTransactionId = viewModel::updateTransactionId,
        onUpdateTransactionIgnoring = viewModel::updateTransactionIgnoring,
        onDeleteTransaction = viewModel::deleteTransaction,
    )
}

@Composable
private fun TransactionOverviewScreen(
    transactionOverviewState: TransactionOverviewUiState,
    showNavigationIcon: Boolean,
    onBackClick: () -> Unit,
    updateTransactionId: (String) -> Unit = {},
    onUpdateTransactionIgnoring: (Boolean) -> Unit = {},
    onDeleteTransaction: () -> Unit = {},
) {
    when (transactionOverviewState) {
        TransactionOverviewUiState.Loading -> LoadingState(Modifier.fillMaxSize())
        is TransactionOverviewUiState.Success -> {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 88.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                transactions(
                    transactionsCategories = transactionOverviewState.transactionsCategories,
                    onTransactionClick = {
                        updateTransactionId(it)
                    },
                )
            }
        }
    }
}