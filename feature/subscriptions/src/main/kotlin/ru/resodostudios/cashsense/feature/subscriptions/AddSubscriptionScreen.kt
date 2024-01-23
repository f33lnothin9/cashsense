package ru.resodostudios.cashsense.feature.subscriptions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.Instant
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.ui.CurrencyExposedDropdownMenuBox
import ru.resodostudios.cashsense.core.ui.formattedDate
import ru.resodostudios.cashsense.core.ui.validateAmount
import ru.resodostudios.cashsense.core.ui.R as uiR

@Composable
internal fun AddSubscriptionRoute(
    onBackClick: () -> Unit,
    viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val subscriptionState by viewModel.subscriptionUiState.collectAsStateWithLifecycle()
    AddSubscriptionScreen(
        subscriptionState = subscriptionState,
        onSubscriptionEvent = viewModel::onSubscriptionEvent,
        onBackClick = onBackClick
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class
)
@Composable
internal fun AddSubscriptionScreen(
    subscriptionState: SubscriptionUiState,
    onSubscriptionEvent: (SubscriptionEvent) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.feature_subscriptions_new_subscription)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = ImageVector.vectorResource(CsIcons.ArrowBack),
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            onSubscriptionEvent(SubscriptionEvent.Confirm)
                            onBackClick()
                        },
                        enabled = subscriptionState.title.isNotBlank() && subscriptionState.amount.validateAmount().second && subscriptionState.paymentDate.isNotBlank()
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(CsIcons.Confirm),
                            contentDescription = stringResource(R.string.feature_subscriptions_add_subscription_icon_description)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        val (titleTextField, amountTextField) = remember { FocusRequester.createRefs() }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = paddingValues,
            modifier = Modifier.padding(16.dp)
        ) {
            item(
                span = { GridItemSpan(2) }
            ) {
                OutlinedTextField(
                    value = subscriptionState.title,
                    onValueChange = { onSubscriptionEvent(SubscriptionEvent.UpdateTitle(it)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    label = { Text(text = stringResource(uiR.string.title)) },
                    placeholder = { Text(text = stringResource(uiR.string.title) + "*") },
                    supportingText = { Text(text = stringResource(uiR.string.required)) },
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(titleTextField)
                        .focusProperties { next = amountTextField }
                )
            }
            item {
                OutlinedTextField(
                    value = subscriptionState.amount,
                    onValueChange = {
                        onSubscriptionEvent(
                            SubscriptionEvent.UpdateAmount(
                                it.validateAmount().first
                            )
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    label = { Text(text = stringResource(uiR.string.amount)) },
                    placeholder = { Text(text = stringResource(uiR.string.amount) + "*") },
                    supportingText = { Text(text = stringResource(uiR.string.required)) },
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(amountTextField)
                )
            }
            item {
                val paymentDatePickerState = rememberDatePickerState()
                var openDialog by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = if (subscriptionState.paymentDate.isNotEmpty()) {
                        formattedDate(Instant.fromEpochMilliseconds(paymentDatePickerState.selectedDateMillis!!))
                    } else {
                        stringResource(uiR.string.none)
                    },
                    onValueChange = { },
                    readOnly = true,
                    label = { Text(text = stringResource(R.string.feature_subscriptions_payment_date)) },
                    placeholder = { Text(text = "${stringResource(R.string.feature_subscriptions_payment_date)}*") },
                    supportingText = { Text(text = stringResource(uiR.string.required)) },
                    trailingIcon = {
                        IconButton(onClick = { openDialog = true }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(CsIcons.Calendar),
                                contentDescription = null
                            )
                        }
                    },
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth()
                )
                if (openDialog) {
                    val confirmEnabled = remember {
                        derivedStateOf { paymentDatePickerState.selectedDateMillis != null }
                    }
                    DatePickerDialog(
                        onDismissRequest = { openDialog = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    openDialog = false
                                    onSubscriptionEvent(
                                        SubscriptionEvent.UpdatePaymentDate(
                                            Instant.fromEpochMilliseconds(paymentDatePickerState.selectedDateMillis!!)
                                                .toString()
                                        )
                                    )
                                },
                                enabled = confirmEnabled.value
                            ) {
                                Text(stringResource(uiR.string.core_ui_ok))
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { openDialog = false }
                            ) {
                                Text(stringResource(uiR.string.core_ui_cancel))
                            }
                        }
                    ) {
                        DatePicker(state = paymentDatePickerState)
                    }
                }
            }
            item {
                CurrencyExposedDropdownMenuBox(
                    currencyName = subscriptionState.currency,
                    onCurrencyClick = { onSubscriptionEvent(SubscriptionEvent.UpdateCurrency(it.name)) }
                )
            }
        }
        LaunchedEffect(Unit) {
            titleTextField.requestFocus()
        }
    }
}