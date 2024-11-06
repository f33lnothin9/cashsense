package ru.resodostudios.cashsense.feature.transfer

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.cleanAndValidateAmount
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun TransferDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransferViewModel = hiltViewModel(),
) {
    val transferState by viewModel.transferState.collectAsStateWithLifecycle()

    TransferDialog(
        transferState = transferState,
        onDismiss = onDismiss,
        onSendingWalletUpdate = viewModel::updateSendingWallet,
        onReceivingWalletUpdate = viewModel::updateReceivingWallet,
        onAmountUpdate = viewModel::updateAmount,
        onExchangingRateUpdate = viewModel::updateExchangingRate,
        modifier = modifier,
    )
}

@Composable
private fun TransferDialog(
    transferState: TransferUiState,
    onDismiss: () -> Unit,
    onSendingWalletUpdate: (TransferWallet) -> Unit,
    onReceivingWalletUpdate: (TransferWallet) -> Unit,
    onAmountUpdate: (String) -> Unit,
    onExchangingRateUpdate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    CsAlertDialog(
        titleRes = localesR.string.new_transfer,
        confirmButtonTextRes = localesR.string.add,
        dismissButtonTextRes = localesR.string.cancel,
        iconRes = CsIcons.SendMoney,
        onConfirm = {},
        isConfirmEnabled = transferState.amount.isNotBlank() && transferState.receivingWallet.id.isNotBlank(),
        onDismiss = onDismiss,
        modifier = modifier,
    ) {
        if (transferState.isLoading) {
            LoadingState(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
            )
        } else {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                WalletDropdownMenu(
                    title = localesR.string.from,
                    onWalletSelect = onSendingWalletUpdate,
                    selectedWallet = transferState.sendingWallet,
                    availableWallets = transferState.transferWallets,
                )
                WalletDropdownMenu(
                    title = localesR.string.to,
                    onWalletSelect = onReceivingWalletUpdate,
                    selectedWallet = transferState.receivingWallet,
                    availableWallets = transferState.transferWallets,
                )
                OutlinedTextField(
                    value = transferState.amount,
                    onValueChange = { onAmountUpdate(it.cleanAndValidateAmount().first) },
                    label = { Text(stringResource(localesR.string.amount)) },
                    placeholder = { Text("0.01") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                    ),
                )
                OutlinedTextField(
                    value = transferState.exchangeRate,
                    onValueChange = { onExchangingRateUpdate(it.cleanAndValidateAmount().first) },
                    label = { Text(stringResource(localesR.string.exchange_rate)) },
                    placeholder = { Text("0.01") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                    ),
                    enabled = transferState.sendingWallet.currency != transferState.receivingWallet.currency,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WalletDropdownMenu(
    @StringRes title: Int,
    onWalletSelect: (TransferWallet) -> Unit,
    selectedWallet: TransferWallet,
    availableWallets: List<TransferWallet>,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = selectedWallet.title,
            onValueChange = {},
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            singleLine = true,
            label = { Text(stringResource(title)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            placeholder = { Text(stringResource(localesR.string.choose_wallet)) },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            availableWallets.forEach { wallet ->
                DropdownMenuItem(
                    text = { Text(wallet.title) },
                    onClick = {
                        onWalletSelect(wallet)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}