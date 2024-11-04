package ru.resodostudios.cashsense.feature.wallet.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.ui.WalletDialog
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun EditWalletDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditWalletViewModel = hiltViewModel(),
) {
    val walletDialogState by viewModel.walletDialogState.collectAsStateWithLifecycle()

    WalletDialog(
        walletDialogState = walletDialogState,
        titleRes = localesR.string.edit_wallet,
        confirmButtonTextRes = localesR.string.save,
        onDismiss = onDismiss,
        onWalletSave = viewModel::saveWallet,
        onTitleUpdate = viewModel::updateTitle,
        onInitialBalanceUpdate = viewModel::updateInitialBalance,
        onCurrencyUpdate = viewModel::updateCurrency,
        onPrimaryUpdate = viewModel::updatePrimary,
        modifier = modifier,
    )
}