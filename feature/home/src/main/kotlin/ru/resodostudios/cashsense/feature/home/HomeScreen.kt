package ru.resodostudios.cashsense.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.AccountBalance
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.ExtendedUserWallet
import ru.resodostudios.cashsense.core.ui.AnimatedAmount
import ru.resodostudios.cashsense.core.ui.EmptyState
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import ru.resodostudios.cashsense.feature.home.WalletsUiState.Empty
import ru.resodostudios.cashsense.feature.home.WalletsUiState.Loading
import ru.resodostudios.cashsense.feature.home.WalletsUiState.Success
import java.math.BigDecimal
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
fun HomeScreen(
    onWalletClick: (String?) -> Unit,
    onTransfer: (String) -> Unit,
    onEditWallet: (String) -> Unit,
    onDeleteWallet: (String) -> Unit,
    highlightSelectedWallet: Boolean = false,
    onTransactionCreate: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    shouldDisplayUndoWallet: Boolean,
    undoWalletRemoval: () -> Unit,
    clearUndoState: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val walletsState by viewModel.walletsUiState.collectAsStateWithLifecycle()
    val financeOverviewState by viewModel.financeOverviewState.collectAsStateWithLifecycle()

    HomeScreen(
        walletsState = walletsState,
        financeOverviewState = financeOverviewState,
        onWalletClick = {
            viewModel.onWalletClick(it)
            onWalletClick(it)
        },
        onTransfer = onTransfer,
        onEditWallet = onEditWallet,
        onDeleteWallet = onDeleteWallet,
        onTransactionCreate = onTransactionCreate,
        highlightSelectedWallet = highlightSelectedWallet,
        onShowSnackbar = onShowSnackbar,
        shouldDisplayUndoWallet = shouldDisplayUndoWallet,
        undoWalletRemoval = undoWalletRemoval,
        clearUndoState = clearUndoState,
    )
}

@Composable
internal fun HomeScreen(
    walletsState: WalletsUiState,
    financeOverviewState: FinanceOverviewUiState,
    onWalletClick: (String?) -> Unit,
    onTransfer: (String) -> Unit,
    onEditWallet: (String) -> Unit,
    onDeleteWallet: (String) -> Unit,
    onTransactionCreate: (String) -> Unit,
    highlightSelectedWallet: Boolean,
    onShowSnackbar: suspend (String, String?) -> Boolean = { _, _ -> false },
    shouldDisplayUndoWallet: Boolean = false,
    undoWalletRemoval: () -> Unit = {},
    clearUndoState: () -> Unit = {},
) {
    val walletDeletedMessage = stringResource(localesR.string.wallet_deleted)
    val undoText = stringResource(localesR.string.undo)

    LaunchedEffect(shouldDisplayUndoWallet) {
        if (shouldDisplayUndoWallet) {
            val snackBarResult = onShowSnackbar(walletDeletedMessage, undoText)
            if (snackBarResult) {
                undoWalletRemoval()
            } else {
                clearUndoState()
            }
        }
    }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        clearUndoState()
    }

    when (walletsState) {
        Loading -> LoadingState(Modifier.fillMaxSize())
        Empty -> EmptyState(localesR.string.home_empty, R.raw.anim_wallets_empty)
        is Success -> {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(300.dp),
                verticalItemSpacing = 16.dp,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 88.dp,
                ),
            ) {
                financeOverviewSection(
                    financeOverviewState = financeOverviewState,
                )
                wallets(
                    extendedUserWallets = walletsState.extendedUserWallets,
                    selectedWalletId = walletsState.selectedWalletId,
                    onWalletClick = onWalletClick,
                    onTransactionCreate = onTransactionCreate,
                    onTransferClick = onTransfer,
                    onEditClick = onEditWallet,
                    onDeleteClick = { walletId ->
                        onDeleteWallet(walletId)
                        onWalletClick(null)
                    },
                    highlightSelectedWallet = highlightSelectedWallet,
                )
            }
        }
    }
}

private fun LazyStaggeredGridScope.wallets(
    extendedUserWallets: List<ExtendedUserWallet>,
    selectedWalletId: String?,
    onWalletClick: (String) -> Unit,
    onTransactionCreate: (String) -> Unit,
    onTransferClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    highlightSelectedWallet: Boolean = false,
) {
    items(
        items = extendedUserWallets,
        key = { it.userWallet.id },
        contentType = { "walletCard" },
    ) { walletData ->
        val selected = highlightSelectedWallet && walletData.userWallet.id == selectedWalletId
        WalletCard(
            userWallet = walletData.userWallet,
            transactions = walletData.transactionsWithCategories.map { it.transaction },
            onWalletClick = onWalletClick,
            onNewTransactionClick = onTransactionCreate,
            onTransferClick = onTransferClick,
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick,
            modifier = Modifier.animateItem(),
            selected = selected,
        )
    }
}

private fun LazyStaggeredGridScope.financeOverviewSection(
    financeOverviewState: FinanceOverviewUiState,
) {
    when (financeOverviewState) {
        FinanceOverviewUiState.NotShown -> Unit
        FinanceOverviewUiState.Loading, is FinanceOverviewUiState.Shown -> {
            item(span = StaggeredGridItemSpan.FullLine) {
                val showBadIndicator = if (financeOverviewState is FinanceOverviewUiState.Shown) {
                    financeOverviewState.showBadIndicator
                } else false
                TotalBalanceCard(
                    showBadIndicator = showBadIndicator,
                    modifier = Modifier.animateItem(),
                ) {
                    if (financeOverviewState is FinanceOverviewUiState.Shown) {
                        val totalBalance = financeOverviewState.totalBalance
                        val userCurrency = financeOverviewState.userCurrency
                        AnimatedAmount(
                            targetState = totalBalance,
                            label = "total_balance",
                        ) {
                            Text(
                                text = totalBalance.formatAmount(userCurrency),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    } else {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp, bottom = 16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TotalBalanceCard(
    showBadIndicator: Boolean,
    modifier: Modifier = Modifier,
    headlineContent: @Composable () -> Unit,
) {
    val color = if (showBadIndicator) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    val borderBrush = remember(color) {
        Brush.verticalGradient(
            colors = listOf(Color.Transparent, color),
            startY = 15.0f,
        )
    }
    val shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
    OutlinedCard(
        shape = shape,
        border = BorderStroke(1.dp, borderBrush),
        modifier = if (showBadIndicator) {
            modifier.shadow(
                ambientColor = color,
                elevation = 12.dp,
                spotColor = color,
                shape = shape,
            )
        } else {
            modifier
        },
    ) {
        CsListItem(
            leadingContent = {
                Icon(
                    imageVector = CsIcons.Outlined.AccountBalance,
                    contentDescription = null,
                )
            },
            headlineContent = headlineContent,
            overlineContent = {
                Text(
                    text = stringResource(localesR.string.total_balance),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        )
    }
}

@Preview
@Composable
fun HomeScreenLoadingPreview() {
    CsTheme {
        Surface {
            HomeScreen(
                walletsState = Loading,
                financeOverviewState = FinanceOverviewUiState.Loading,
                onWalletClick = {},
                onTransfer = {},
                onEditWallet = {},
                onDeleteWallet = {},
                onTransactionCreate = {},
                highlightSelectedWallet = false,
            )
        }
    }
}

@Preview
@Composable
fun HomeScreenEmptyPreview() {
    CsTheme {
        Surface {
            HomeScreen(
                walletsState = Empty,
                financeOverviewState = FinanceOverviewUiState.NotShown,
                onWalletClick = {},
                onTransfer = {},
                onEditWallet = {},
                onDeleteWallet = {},
                onTransactionCreate = {},
                highlightSelectedWallet = false,
            )
        }
    }
}

@Preview
@Composable
fun HomeScreenPopulatedPreview(
    @PreviewParameter(ExtendedUserWalletPreviewParameterProvider::class)
    extendedUserWallets: List<ExtendedUserWallet>,
) {
    CsTheme {
        Surface {
            HomeScreen(
                walletsState = Success(
                    selectedWalletId = null,
                    extendedUserWallets = extendedUserWallets,
                ),
                financeOverviewState = FinanceOverviewUiState.Shown(
                    totalBalance = BigDecimal(5000),
                    userCurrency = getUsdCurrency(),
                    showBadIndicator = true,
                ),
                onWalletClick = {},
                onTransfer = {},
                onEditWallet = {},
                onDeleteWallet = {},
                onTransactionCreate = {},
                highlightSelectedWallet = false,
            )
        }
    }
}