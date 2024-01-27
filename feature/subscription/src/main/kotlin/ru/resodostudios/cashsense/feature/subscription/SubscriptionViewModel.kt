package ru.resodostudios.cashsense.feature.subscription

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
        if (subscriptionId != null) {
            viewModelScope.launch {
                subscriptionsRepository.getSubscription(subscriptionId)
                    .onEach {
                        _subscriptionUiState.emit(
                            SubscriptionUiState(
                                isEditing = true,
                                title = it.title,
                                amount = it.amount.toString(),
                                paymentDate = it.paymentDate.toString(),
                                currency = it.currency
                            )
                        )
                    }
                    .collect()
            }
        }
    }

    fun onSubscriptionEvent(subscriptionEvent: SubscriptionEvent) {
        when (subscriptionEvent) {
            SubscriptionEvent.Confirm -> {
                val subscription = Subscription(
                    id = subscriptionId ?: UUID.randomUUID().toString(),
                    title = _subscriptionUiState.value.title,
                    amount = _subscriptionUiState.value.amount.toBigDecimal(),
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
}

data class SubscriptionUiState(
    val title: String = "",
    val amount: String = "",
    val paymentDate: String = "",
    val currency: String = "USD",
    val isEditing: Boolean = false
)