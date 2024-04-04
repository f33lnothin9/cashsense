package ru.resodostudios.cashsense.feature.subscription.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import ru.resodostudios.cashsense.core.ui.DatePickerTextField
import ru.resodostudios.cashsense.core.ui.formatDate
import ru.resodostudios.cashsense.core.ui.validateAmount
import ru.resodostudios.cashsense.core.ui.R as uiR

@Composable
internal fun SubscriptionRoute(
    onBackClick: () -> Unit,
    viewModel: SubscriptionViewModel = hiltViewModel(),
) {
    val subscriptionState by viewModel.subscriptionUiState.collectAsStateWithLifecycle()

    SubscriptionScreen(
        subscriptionState = subscriptionState,
        onSubscriptionEvent = viewModel::onSubscriptionEvent,
        onBackClick = onBackClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SubscriptionScreen(
    subscriptionState: SubscriptionUiState,
    onSubscriptionEvent: (SubscriptionEvent) -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            val titleRes =
                if (subscriptionState.isEditing) R.string.feature_subscription_detail_edit else R.string.feature_subscription_detail_new

            TopAppBar(
                title = { Text(stringResource(titleRes)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = ImageVector.vectorResource(CsIcons.ArrowBack),
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            onSubscriptionEvent(SubscriptionEvent.Confirm)
                            onBackClick()
                        },
                        enabled = subscriptionState.title.isNotBlank() && subscriptionState.amount.validateAmount().second,
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(CsIcons.Confirm),
                            contentDescription = stringResource(R.string.feature_subscription_detail_add_subscription_icon_description),
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
            modifier = Modifier.padding(16.dp),
        ) {
            item(
                span = { GridItemSpan(2) }
            ) {
                OutlinedTextField(
                    value = subscriptionState.title,
                    onValueChange = { onSubscriptionEvent(SubscriptionEvent.UpdateTitle(it)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                    label = { Text(stringResource(uiR.string.title)) },
                    placeholder = { Text(stringResource(uiR.string.title) + "*") },
                    supportingText = { Text(stringResource(uiR.string.required)) },
                    maxLines = 1,
                    modifier = Modifier
                        .focusRequester(titleTextField)
                        .focusProperties { next = amountTextField },
                )
            }
            item {
                OutlinedTextField(
                    value = subscriptionState.amount,
                    onValueChange = { onSubscriptionEvent(SubscriptionEvent.UpdateAmount(it.validateAmount().first)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done,
                    ),
                    label = { Text(stringResource(uiR.string.amount)) },
                    placeholder = { Text(stringResource(uiR.string.amount) + "*") },
                    supportingText = { Text(stringResource(uiR.string.required)) },
                    maxLines = 1,
                    modifier = Modifier.focusRequester(amountTextField),
                )
            }
            item {
                DatePickerTextField(
                    value = subscriptionState.paymentDate.formatDate(),
                    labelTextId = R.string.feature_subscription_detail_payment_date,
                    iconId = CsIcons.Calendar,
                    modifier = Modifier.fillMaxWidth(),
                    initialSelectedDateMillis = subscriptionState.paymentDate.toEpochMilliseconds(),
                    onDateClick = { onSubscriptionEvent(SubscriptionEvent.UpdatePaymentDate(Instant.fromEpochMilliseconds(it))) },
                )
            }
            item {
                CurrencyExposedDropdownMenuBox(
                    currencyName = subscriptionState.currency,
                    onCurrencyClick = { onSubscriptionEvent(SubscriptionEvent.UpdateCurrency(it.name)) },
                )
            }
        }
        LaunchedEffect(Unit) {
            if (!subscriptionState.isEditing) {
                titleTextField.requestFocus()
            }
        }
    }
}