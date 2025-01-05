package ru.resodostudios.cashsense.feature.transfer

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.Clock
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.StatusType
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.ui.OutlinedAmountField
import ru.resodostudios.cashsense.core.ui.util.cleanAmount
import ru.resodostudios.cashsense.core.ui.util.formatAmount
import ru.resodostudios.cashsense.core.ui.util.isAmountValid
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import java.math.BigDecimal
import kotlin.uuid.Uuid
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun TransferDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransferDialogViewModel = hiltViewModel(),
) {
    val transferState by viewModel.transferDialogState.collectAsStateWithLifecycle()

    TransferDialog(
        transferState = transferState,
        onDismiss = onDismiss,
        onSendingWalletUpdate = viewModel::updateSendingWallet,
        onReceivingWalletUpdate = viewModel::updateReceivingWallet,
        onAmountUpdate = viewModel::updateAmount,
        onExchangingRateUpdate = viewModel::updateExchangingRate,
        onConvertedAmountUpdate = viewModel::updateConvertedAmount,
        onTransferSave = viewModel::saveTransfer,
        modifier = modifier,
    )
}

@Composable
private fun TransferDialog(
    transferState: TransferDialogUiState,
    onDismiss: () -> Unit,
    onSendingWalletUpdate: (TransferWallet) -> Unit,
    onReceivingWalletUpdate: (TransferWallet) -> Unit,
    onAmountUpdate: (String) -> Unit,
    onExchangingRateUpdate: (String) -> Unit,
    onConvertedAmountUpdate: (String) -> Unit,
    onTransferSave: (Transaction, Transaction) -> Unit,
    modifier: Modifier = Modifier,
) {
    CsAlertDialog(
        titleRes = localesR.string.new_transfer,
        confirmButtonTextRes = localesR.string.transfer,
        dismissButtonTextRes = localesR.string.cancel,
        iconRes = CsIcons.SendMoney,
        onConfirm = {
            val transferId = Uuid.random()
            val timestamp = Clock.System.now()
            val withdrawalAmount = BigDecimal(transferState.amount)
            val withdrawalTransaction = Transaction(
                id = Uuid.random().toHexString(),
                walletOwnerId = transferState.sendingWallet.id,
                description = null,
                amount = withdrawalAmount.negate(),
                timestamp = timestamp,
                status = StatusType.COMPLETED,
                ignored = true,
                transferId = transferId,
            )
            val depositTransaction = Transaction(
                id = Uuid.random().toHexString(),
                walletOwnerId = transferState.receivingWallet.id,
                description = null,
                amount = BigDecimal(transferState.convertedAmount),
                timestamp = timestamp,
                status = StatusType.COMPLETED,
                ignored = true,
                transferId = transferId,
            )
            onTransferSave(withdrawalTransaction, depositTransaction)
            onDismiss()
        },
        isConfirmEnabled = transferState.amount.isAmountValid() &&
                transferState.sendingWallet.id.isNotBlank() &&
                transferState.receivingWallet.id.isNotBlank() &&
                transferState.exchangeRate.isAmountValid() &&
                transferState.convertedAmount.isAmountValid() &&
                transferState.sendingWallet != transferState.receivingWallet,
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
            val focusManager = LocalFocusManager.current
            val exchangeRateEnabled =
                transferState.sendingWallet.currency != transferState.receivingWallet.currency

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                WalletDropdownMenu(
                    title = localesR.string.from_wallet,
                    onWalletSelect = onSendingWalletUpdate,
                    selectedWallet = transferState.sendingWallet,
                    availableWallets = transferState.transferWallets,
                    modifier = Modifier.fillMaxWidth(),
                )
                WalletDropdownMenu(
                    title = localesR.string.to_wallet,
                    onWalletSelect = onReceivingWalletUpdate,
                    selectedWallet = transferState.receivingWallet,
                    availableWallets = transferState.transferWallets,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedAmountField(
                    value = transferState.amount,
                    onValueChange = onAmountUpdate,
                    labelRes = localesR.string.amount,
                    currency = transferState.sendingWallet.currency ?: getUsdCurrency(),
                    imeAction = if (exchangeRateEnabled) ImeAction.Next else ImeAction.Done,
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) },
                        onDone = { focusManager.clearFocus() },
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = transferState.exchangeRate,
                    onValueChange = { onExchangingRateUpdate(it.cleanAmount()) },
                    label = { Text(stringResource(localesR.string.exchange_rate)) },
                    placeholder = { Text("0.01") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done,
                    ),
                    enabled = exchangeRateEnabled,
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() },
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                AnimatedVisibility(transferState.receivingWallet.currency != null) {
                    OutlinedAmountField(
                        value = transferState.convertedAmount,
                        onValueChange = onConvertedAmountUpdate,
                        labelRes = localesR.string.converted_amount,
                        currency = transferState.receivingWallet.currency!!,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
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
            modifier = modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            readOnly = true,
            singleLine = true,
            label = { Text(stringResource(title)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            placeholder = { Text(stringResource(localesR.string.choose_wallet)) },
            supportingText = if (selectedWallet.currency != null) {
                {
                    Text(selectedWallet.currentBalance.formatAmount(selectedWallet.currency))
                }
            } else {
                null
            },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            availableWallets.forEach { wallet ->
                val currentBalance = wallet.currency?.let { wallet.currentBalance.formatAmount(it) }
                val menuText = "${wallet.title} – $currentBalance"
                DropdownMenuItem(
                    text = {
                        Text(
                            text = menuText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
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