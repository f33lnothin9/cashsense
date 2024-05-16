package ru.resodostudios.cashsense.feature.subscription.dialog

import android.app.AlarmManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import ru.resodostudios.cashsense.core.data.repository.SubscriptionsRepository
import ru.resodostudios.cashsense.core.model.data.Currency
import ru.resodostudios.cashsense.core.model.data.Reminder
import ru.resodostudios.cashsense.core.model.data.Subscription
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SubscriptionDialogViewModel @Inject constructor(
    private val subscriptionsRepository: SubscriptionsRepository,
) : ViewModel() {

    private val _subscriptionDialogUiState = MutableStateFlow(SubscriptionDialogUiState())
    val subscriptionDialogUiState = _subscriptionDialogUiState.asStateFlow()

    fun onSubscriptionEvent(event: SubscriptionDialogEvent) {
        when (event) {
            SubscriptionDialogEvent.Save -> {
                val subscriptionId = _subscriptionDialogUiState.value.id.ifEmpty { UUID.randomUUID().toString() }

                val timeZone = TimeZone.currentSystemDefault()
                val currentInstant = _subscriptionDialogUiState.value.paymentDate
                val currentDateTime = currentInstant.toLocalDateTime(timeZone)
                val previousDate = currentDateTime.date.minus(1, DateTimeUnit.DAY)
                val previousDayAtNineAm = LocalDateTime(previousDate, LocalTime(9, 0))
                    .toInstant(timeZone)

                val subscription = Subscription(
                    id = subscriptionId,
                    title = _subscriptionDialogUiState.value.title,
                    amount = _subscriptionDialogUiState.value.amount.toBigDecimal(),
                    paymentDate = _subscriptionDialogUiState.value.paymentDate,
                    currency = _subscriptionDialogUiState.value.currency,
                    reminder = Reminder(
                        id = subscriptionId.hashCode(),
                        notificationDate = previousDayAtNineAm,
                        repeatingInterval = 30 * AlarmManager.INTERVAL_DAY,
                    ),
                )
                viewModelScope.launch {
                    subscriptionsRepository.upsertSubscription(subscription)
                }
                _subscriptionDialogUiState.update {
                    SubscriptionDialogUiState()
                }
            }

            is SubscriptionDialogEvent.UpdateId -> {
                _subscriptionDialogUiState.update {
                    it.copy(id = event.id)
                }
                loadSubscription()
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
        }
    }

    private fun loadSubscription() {
        viewModelScope.launch {
            subscriptionsRepository.getSubscription(_subscriptionDialogUiState.value.id)
                .onStart { _subscriptionDialogUiState.value = SubscriptionDialogUiState(isLoading = true) }
                .catch { _subscriptionDialogUiState.value = SubscriptionDialogUiState() }
                .collect {
                    _subscriptionDialogUiState.value = SubscriptionDialogUiState(
                        id = it.id,
                        title = it.title,
                        amount = it.amount.toString(),
                        paymentDate = it.paymentDate,
                        currency = it.currency,
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
    val currency: String = Currency.USD.name,
    val isLoading: Boolean = false,
)