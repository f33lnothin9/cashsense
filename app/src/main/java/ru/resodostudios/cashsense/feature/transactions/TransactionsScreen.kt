package ru.resodostudios.cashsense.feature.transactions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import ru.resodostudios.cashsense.R
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.Transaction

@Composable
internal fun TransactionsRoute(
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    val transactionsState by viewModel.transactionsUiState.collectAsStateWithLifecycle()
    TransactionsScreen(
        transactionsState = transactionsState,
        onDelete = viewModel::deleteCategory
    )
}

@Composable
internal fun TransactionsScreen(
    transactionsState: TransactionsUiState,
    onDelete: (Transaction) -> Unit
) {

    when (transactionsState) {
        TransactionsUiState.Loading -> LoadingState()
        is TransactionsUiState.Success -> if (transactionsState.transactions.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(300.dp),
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    transactions(
                        transactionsState = transactionsState,
                        onDelete = onDelete
                    )
                }
            }
        } else {
            EmptyState()
        }
    }
}

private fun LazyGridScope.transactions(
    transactionsState: TransactionsUiState,
    onDelete: (Transaction) -> Unit
) {
    when (transactionsState) {
        TransactionsUiState.Loading -> Unit
        is TransactionsUiState.Success -> {
            items(transactionsState.transactions) { transaction ->
                ListItem(
                    headlineContent = { Text(text = transaction.name) },
                    trailingContent = {
                        IconButton(onClick = { onDelete(transaction) }) {
                            Icon(
                                imageVector = CsIcons.Delete,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState() {

    val lottieComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.anim_empty))

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LottieAnimation(
            modifier = Modifier
                .align(Alignment.Center)
                .size(256.dp),
            composition = lottieComposition,
        )
    }
}