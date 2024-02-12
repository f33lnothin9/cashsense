package ru.resodostudios.cashsense.feature.subscription

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.toInstant
import ru.resodostudios.cashsense.core.data.repository.SubscriptionsRepository
import ru.resodostudios.cashsense.core.model.data.Subscription
import ru.resodostudios.cashsense.feature.subscription.navigation.SubscriptionArgs
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val subscriptionsRepository: SubscriptionsRepository
) : ViewModel() {

    private val subscriptionArgs: SubscriptionArgs = SubscriptionArgs(savedStateHandle)
    private val subscriptionId: String? = subscriptionArgs.subscriptionId

    private val _subscriptionUiState = MutableStateFlow(SubscriptionUiState())
    val subscriptionUiState = _subscriptionUiState.asStateFlow()

    init {
        loadSubscription()
    }

    fun onSubscriptionEvent(subscriptionEvent: SubscriptionEvent) {
        when (subscriptionEvent) {
            SubscriptionEvent.Confirm -> {
                val subscription = Subscription(
                    id = subscriptionId ?: UUID.randomUUID().toString(),
                    title = _subscriptionUiState.value.title.text,
                    amount = _subscriptionUiState.value.amount.text.toBigDecimal(),
                    paymentDate = _subscriptionUiState.value.paymentDate.toInstant(),
                    currency = _subscriptionUiState.value.currency,
                    notificationDate = null,
                    repeatingInterval = null
                )
                viewModelScope.launch {
                    subscriptionsRepository.upsertSubscription(subscription)
                }
            }

            is SubscriptionEvent.UpdateAmount -> {
                _subscriptionUiState.update {
                    it.copy(amount = subscriptionEvent.amount)
                }
            }

            is SubscriptionEvent.UpdateCurrency -> {
                _subscriptionUiState.update {
                    it.copy(currency = subscriptionEvent.currency)
                }
            }

            is SubscriptionEvent.UpdatePaymentDate -> {
                _subscriptionUiState.update {
                    it.copy(paymentDate = subscriptionEvent.paymentDate)
                }
            }

            is SubscriptionEvent.UpdateTitle -> {
                _subscriptionUiState.update {
                    it.copy(title = subscriptionEvent.title)
                }
            }
        }
    }

    private fun loadSubscription() {
        if (subscriptionId != null) {
            viewModelScope.launch {
                subscriptionsRepository.getSubscription(subscriptionId)
                    .onEach {
                        _subscriptionUiState.emit(
                            SubscriptionUiState(
                                title = TextFieldValue(
                                    text = it.title,
                                    selection = TextRange(it.title.length)
                                ),
                                amount = TextFieldValue(
                                    text = it.amount.toString(),
                                    selection = TextRange(it.amount.toString().length)
                                ),
                                paymentDate = it.paymentDate.toString(),
                                currency = it.currency,
                                isEditing = true,
                            )
                        )
                    }
                    .collect()
            }
        }
    }
}