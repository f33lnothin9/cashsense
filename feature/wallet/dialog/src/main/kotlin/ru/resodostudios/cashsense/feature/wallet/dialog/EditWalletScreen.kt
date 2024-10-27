package ru.resodostudios.cashsense.feature.wallet.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.ui.CurrencyDropdownMenu
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
fun EditWalletScreen(
    walletId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditWalletViewModel = hiltViewModel(),
) {
    val editWalletState by viewModel.editWalletUiState.collectAsStateWithLifecycle()

    LaunchedEffect(walletId) {
        viewModel.loadWallet(walletId)
    }

    EditWalletScreen(
        editWalletState = editWalletState,
        onBackClick = onBackClick,
        onSaveClick = viewModel::saveWallet,
        onTitleUpdate = viewModel::updateTitle,
        onInitialBalanceUpdate = viewModel::updateInitialBalance,
        onCurrencyUpdate = viewModel::updateCurrency,
        onPrimaryUpdate = viewModel::updatePrimary,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWalletScreen(
    editWalletState: EditWalletUiState,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onTitleUpdate: (String) -> Unit,
    onInitialBalanceUpdate: (String) -> Unit,
    onCurrencyUpdate: (String) -> Unit,
    onPrimaryUpdate: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    if (editWalletState.isLoading) {
        LoadingState(Modifier.fillMaxWidth())
    } else {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 88.dp),
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(localesR.string.edit_wallet),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(CsIcons.ArrowBack),
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                    IconButton(
                        enabled = editWalletState.title.isNotBlank(),
                        onClick = {
                            onSaveClick()
                            onBackClick()
                        },
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(CsIcons.Check),
                            contentDescription = stringResource(localesR.string.add_transaction_icon_description),
                        )
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
            )
            Column(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            ) {
                OutlinedTextField(
                    value = editWalletState.title,
                    onValueChange = onTitleUpdate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    label = { Text(stringResource(localesR.string.title)) },
                    placeholder = { Text(stringResource(localesR.string.title) + "*") },
                    supportingText = { Text(stringResource(localesR.string.required)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = editWalletState.initialBalance,
                    onValueChange = onInitialBalanceUpdate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    label = { Text(stringResource(localesR.string.initial_balance)) },
                    placeholder = { Text("0") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    singleLine = true,
                )
                CurrencyDropdownMenu(
                    currencyCode = editWalletState.currency,
                    onCurrencyClick = { onCurrencyUpdate(it.currencyCode) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                )
                CsListItem(
                    headlineContent = { Text(stringResource(localesR.string.primary)) },
                    leadingContent = {
                        Icon(
                            imageVector = ImageVector.vectorResource(CsIcons.Star),
                            contentDescription = null,
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = editWalletState.isPrimary,
                            onCheckedChange = onPrimaryUpdate,
                        )
                    }
                )
            }
        }
    }
}