package ru.resodostudios.cashsense.feature.transaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
internal fun TransactionRoute(
    onBackClick: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel(),
) {
    val transactionState by viewModel.transactionUiState.collectAsStateWithLifecycle()

    TransactionScreen()
}

@Composable
internal fun TransactionScreen() {

}