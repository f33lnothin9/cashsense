package ru.resodostudios.cashsense.feature.wallets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import ru.resodostudios.cashsense.core.ui.component.LoadingState

@Composable
internal fun WalletsScreen(
    viewModel: WalletsViewModel = hiltViewModel(),
) {
    val walletsState by viewModel.walletsUiState.collectAsStateWithLifecycle()

    WalletsScreen(
        walletsState = walletsState,
    )
}

@Composable
private fun WalletsScreen(
    walletsState: WalletsUiState,
) {
    val listState = rememberScalingLazyListState()

    when (walletsState) {
        WalletsUiState.Empty -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Empty",
                )
            }
        }

        WalletsUiState.Loading -> LoadingState()
        is WalletsUiState.Success -> {
            ScreenScaffold(scrollState = listState) {
                ScalingLazyColumn(
                    state = listState,
                ) {
                    items(walletsState.extendedUserWallets) { extendedUserWallet ->
                        Card(onClick = {}) {
                            Text(extendedUserWallet.userWallet.title)
                        }
                    }
                }
            }
        }
    }
}