package ru.resodostudios.cashsense.feature.transfer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsAlertDialog
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.ui.LoadingState
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
        modifier = modifier,
    )
}

@Composable
private fun TransferDialog(
    transferState: TransferUiState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CsAlertDialog(
        titleRes = localesR.string.new_transfer,
        confirmButtonTextRes = localesR.string.add,
        dismissButtonTextRes = localesR.string.cancel,
        iconRes = CsIcons.SendMoney,
        onConfirm = {},
        isConfirmEnabled = transferState.amount.isNotBlank() && transferState.toWallet.id.isNotBlank(),
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
            Column(Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = transferState.fromWallet.title,
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    label = { Text(stringResource(localesR.string.from_wallet)) },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = transferState.toWallet.title,
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    label = { Text(stringResource(localesR.string.to_wallet)) },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = transferState.amount,
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    label = { Text(stringResource(localesR.string.amount)) },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = transferState.exchangeRate,
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    label = { Text(stringResource(localesR.string.exchange_rate)) },
                    singleLine = true,
                )
            }
        }
    }
}