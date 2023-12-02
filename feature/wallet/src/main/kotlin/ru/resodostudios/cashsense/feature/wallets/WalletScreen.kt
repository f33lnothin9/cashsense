package ru.resodostudios.cashsense.feature.wallets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.feature.transactions.transactions

@Composable
internal fun WalletRoute(
    viewModel: WalletViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val walletState by viewModel.walletUiState.collectAsStateWithLifecycle()

    WalletScreen(
        walletState = walletState,
        onBackClick = onBackClick,
        onDelete = { TODO() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WalletScreen(
    walletState: WalletUiState,
    onBackClick: () -> Unit,
    onDelete: (Transaction) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    when (walletState) {
        WalletUiState.Loading -> LoadingState()
        is WalletUiState.Success -> {
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    TopAppBar(
                        title = { Text(text = walletState.walletWithTransactions.wallet.title) },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(imageVector = CsIcons.ArrowBack, contentDescription = null)
                            }
                        }
                    )
                },
                contentWindowInsets = WindowInsets.waterfall,
                content = { innerPadding ->
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(300.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = innerPadding
                    ) {
                        transactions(
                            transactions = walletState.walletWithTransactions.transactions,
                            onDelete = onDelete
                        )
                    }
                }
            )
        }
    }
}