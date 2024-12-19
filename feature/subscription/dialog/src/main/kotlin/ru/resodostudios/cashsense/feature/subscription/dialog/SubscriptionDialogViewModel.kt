package ru.resodostudios.cashsense.feature.subscription.dialog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import ru.resodostudios.cashsense.core.data.repository.SubscriptionsRepository
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.model.data.RepeatingIntervalType
import ru.resodostudios.cashsense.core.model.data.RepeatingIntervalType.NONE
import ru.resodostudios.cashsense.core.model.data.getRepeatingIntervalType
import ru.resodostudios.cashsense.core.network.di.ApplicationScope
import ru.resodostudios.cashsense.core.util.getUsdCurrency
import ru.resodostudios.cashsense.feature.subscription.dialog.navigation.SubscriptionDialogRoute
import java.util.Currency
import javax.inject.Inject

@HiltViewModel
class SubscriptionDialogViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val subscriptionsRepository: SubscriptionsRepository,
    savedStateHandle: SavedStateHandle,
    @ApplicationScope private val appScope: CoroutineScope,
) : ViewModel() {

    private val subscriptionDialogDestination: SubscriptionDialogRoute = savedStateHandle.toRoute()

    private val _subscriptionDialogUiState = MutableStateFlow(SubscriptionDialogUiState())
    val subscriptionDialogUiState: StateFlow<SubscriptionDialogUiState>
        get() = _subscriptionDialogUiState.asStateFlow()

    init {
        if (subscriptionDialogDestination.subscriptionId != null) {
            loadSubscription(subscriptionDialogDestination.subscriptionId)
        } else {
            loadUserData()
        }
    }

    fun onSubscriptionEvent(event: SubscriptionDialogEvent) {
        when (event) {
            is SubscriptionDialogEvent.Save -> {
                appScope.launch {
                    subscriptionsRepository.upsertSubscription(event.subscription)
                }
            }

            is SubscriptionDialogEvent.UpdateTitle -> {
                _subscriptionDialogUiState.update {
                    it.copy(title = event.title)
                }
            }

            is SubscriptionDialogEvent.UpdateAmount -> {
                _subscriptionDialogUiState.update {
                    it.copy(amount = event.amount)
                }
            }

            is SubscriptionDialogEvent.UpdateCurrency -> {
                _subscriptionDialogUiState.update {
                    it.copy(currency = event.currency)
                }
            }

            is SubscriptionDialogEvent.UpdatePaymentDate -> {
                _subscriptionDialogUiState.update {
                    it.copy(paymentDate = event.paymentDate)
                }
            }

            is SubscriptionDialogEvent.UpdateReminderSwitch -> {
                _subscriptionDialogUiState.update {
                    it.copy(isReminderEnabled = event.isReminderActive)
                }
            }

            is SubscriptionDialogEvent.UpdateRepeatingInterval -> {
                _subscriptionDialogUiState.update {
                    it.copy(repeatingInterval = event.repeatingInterval)
                }
            }
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _subscriptionDialogUiState.update { SubscriptionDialogUiState(isLoading = true) }
            val userData = userDataRepository.userData.first()
            _subscriptionDialogUiState.update {
                SubscriptionDialogUiState(
                    currency = Currency.getInstance(userData.currency),
                )
            }
        }
    }

    private fun loadSubscription(id: String) {
        viewModelScope.launch {
            _subscriptionDialogUiState.update {
                SubscriptionDialogUiState(
                    id = id,
                    isLoading = true,
                )
            }
            val subscription = subscriptionsRepository.getSubscription(id).first()
            _subscriptionDialogUiState.update {
                it.copy(
                    title = subscription.title,
                    amount = subscription.amount.toString(),
                    paymentDate = subscription.paymentDate,
                    currency = subscription.currency,
                    isReminderEnabled = subscription.reminder != null,
                    repeatingInterval = getRepeatingIntervalType(subscription.reminder?.repeatingInterval),
                    isLoading = false,
                )
            }
        }
    }
}

data class SubscriptionDialogUiState(
    val id: String = "",
    val title: String = "",
    val amount: String = "",
    val paymentDate: Instant = Clock.System.now(),
    val currency: Currency = getUsdCurrency(),
    val isReminderEnabled: Boolean = false,
    val repeatingInterval: RepeatingIntervalType = NONE,
    val isLoading: Boolean = false,
)