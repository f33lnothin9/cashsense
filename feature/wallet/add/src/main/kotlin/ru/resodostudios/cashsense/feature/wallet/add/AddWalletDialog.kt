package ru.resodostudios.cashsense.feature.wallet.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.ui.WalletDialog
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
fun AddWalletDialog(
    onDismiss: () -> Unit,
    viewModel: AddWalletViewModel = hiltViewModel(),
) {
    val walletDialogState by viewModel.walletDialogUiState.collectAsStateWithLifecycle()

    WalletDialog(
        walletDialogState = walletDialogState,
        titleRes = localesR.string.new_wallet,
        confirmButtonTextRes = localesR.string.add,
        onDismiss = onDismiss,
        onWalletSave = {
            viewModel.saveWallet()
            onDismiss()
        },
        onTitleUpdate = viewModel::updateTitle,
        onInitialBalanceUpdate = viewModel::updateInitialBalance,
        onCurrencyUpdate = viewModel::updateCurrency,
        onPrimaryUpdate = viewModel::updatePrimary,
    )
}