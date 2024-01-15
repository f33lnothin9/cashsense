package ru.resodostudios.cashsense.feature.subscriptions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.toInstant
import ru.resodostudios.cashsense.core.data.repository.SubscriptionsRepository
import ru.resodostudios.cashsense.core.model.data.Subscription
import ru.resodostudios.cashsense.feature.subscriptions.navigation.SubscriptionArgs
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val subscriptionsRepository: SubscriptionsRepository
) : ViewModel() {

    private val subscriptionArgs: SubscriptionArgs = SubscriptionArgs(savedStateHandle)

    private val subscriptionId = subscriptionArgs.subscriptionId

    private val _subscriptionState = MutableStateFlow(SubscriptionState())

    val subscriptionState = _subscriptionState.asStateFlow()

    val subscriptionUiState: StateFlow<SubscriptionUiState> =
        subscriptionsRepository.getSubscription(UUID.fromString(subscriptionId))
            .map<Subscription, SubscriptionUiState>(SubscriptionUiState::Success)
            .onStart { emit(SubscriptionUiState.Loading) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SubscriptionUiState.Loading
            )

    fun onSubscriptionEvent(subscriptionEvent: SubscriptionEvent) {
        when (subscriptionEvent) {
            SubscriptionEvent.Confirm -> {
                val subscription = Subscription(
                    subscriptionId = UUID.randomUUID(),
                    title = _subscriptionState.value.title,
                    amount = _subscriptionState.value.amount.toBigDecimal(),
                    paymentDate = _subscriptionState.value.paymentDate.toInstant(),
                    currency = _subscriptionState.value.currency,
                    notificationDate = null,
                    repeatingInterval = null,
                )
                viewModelScope.launch {
                    subscriptionsRepository.upsertSubscription(subscription)
                }
            }
            is SubscriptionEvent.UpdateAmount -> {
                _subscriptionState.update {
                    it.copy(
                        amount = subscriptionEvent.amount
                    )
                }
            }
            is SubscriptionEvent.UpdateCurrency -> {
                _subscriptionState.update {
                    it.copy(
                        currency = subscriptionEvent.currency
                    )
                }
            }
            is SubscriptionEvent.UpdatePaymentDate -> {
                _subscriptionState.update {
                    it.copy(
                        paymentDate = subscriptionEvent.paymentDate
                    )
                }
            }
            is SubscriptionEvent.UpdateTitle -> {
                _subscriptionState.update {
                    it.copy(
                        title = subscriptionEvent.title
                    )
                }
            }
        }
    }

    fun upsertSubscription(subscription: Subscription) {
        viewModelScope.launch {
            subscriptionsRepository.upsertSubscription(subscription)
        }
    }
}

sealed interface SubscriptionUiState {
    data object Loading : SubscriptionUiState

    data class Success(
        val subscription: Subscription
    ) : SubscriptionUiState
}