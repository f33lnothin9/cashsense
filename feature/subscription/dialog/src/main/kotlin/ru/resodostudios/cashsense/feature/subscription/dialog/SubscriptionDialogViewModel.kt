package ru.resodostudios.cashsense.feature.subscription.dialog

import android.app.AlarmManager.INTERVAL_DAY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.model.data.Reminder
import ru.resodostudios.cashsense.core.model.data.Subscription
import ru.resodostudios.cashsense.feature.subscription.dialog.RepeatingIntervalType.NONE
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SubscriptionDialogViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val subscriptionsRepository: SubscriptionsRepository,
) : ViewModel() {

    private val _subscriptionDialogUiState = MutableStateFlow(SubscriptionDialogUiState())
    val subscriptionDialogUiState: StateFlow<SubscriptionDialogUiState>
        get() = _subscriptionDialogUiState.asStateFlow()

    init {
        if (_subscriptionDialogUiState.value.id.isEmpty()) clearSubscriptionDialogState()
    }

    fun onSubscriptionEvent(event: SubscriptionDialogEvent) {
        when (event) {
            SubscriptionDialogEvent.Save -> {
                val subscriptionId = _subscriptionDialogUiState.value.id.ifEmpty { UUID.randomUUID().toString() }
                var reminder: Reminder? = null

                if (_subscriptionDialogUiState.value.isReminderEnabled) {
                    val timeZone = TimeZone.currentSystemDefault()
                    val currentInstant = _subscriptionDialogUiState.value.paymentDate
                    val currentDateTime = currentInstant.toLocalDateTime(timeZone)
                    val previousDate = currentDateTime.date.minus(1, DateTimeUnit.DAY)
                    val notificationDate = LocalDateTime(previousDate, LocalTime(9, 0))
                        .toInstant(timeZone)

                    reminder = Reminder(
                        id = subscriptionId.hashCode(),
                        notificationDate = notificationDate,
                        repeatingInterval = _subscriptionDialogUiState.value.repeatingInterval.period,
                    )
                }

                val subscription = Subscription(
                    id = subscriptionId,
                    title = _subscriptionDialogUiState.value.title,
                    amount = _subscriptionDialogUiState.value.amount.toBigDecimal(),
                    paymentDate = _subscriptionDialogUiState.value.paymentDate,
                    currency = _subscriptionDialogUiState.value.currency,
                    reminder = reminder,
                )
                viewModelScope.launch {
                    subscriptionsRepository.upsertSubscription(subscription)
                }
                clearSubscriptionDialogState()
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
                        isReminderEnabled = it.reminder != null,
                        repeatingInterval = getRepeatingIntervalType(it.reminder?.repeatingInterval ?: 0),
                        isLoading = false,
                    )
                }
        }
    }

    private fun clearSubscriptionDialogState() {
        viewModelScope.launch {
            userDataRepository.userData
                .onStart { _subscriptionDialogUiState.value = SubscriptionDialogUiState(isLoading = true) }
                .collect {
                    _subscriptionDialogUiState.value = SubscriptionDialogUiState(
                        currency = it.currency,
                    )
                }
        }
    }
}

fun getRepeatingIntervalType(repeatingInterval: Long): RepeatingIntervalType =
    RepeatingIntervalType.entries.firstOrNull { it.period == repeatingInterval } ?: NONE

enum class RepeatingIntervalType(val period: Long) {
    NONE(0L),
    DAILY(INTERVAL_DAY),
    WEEKLY(7 * INTERVAL_DAY),
    MONTHLY(30 * INTERVAL_DAY),
    YEARLY(365 * INTERVAL_DAY),
}

data class SubscriptionDialogUiState(
    val id: String = "",
    val title: String = "",
    val amount: String = "",
    val paymentDate: Instant = Clock.System.now(),
    val currency: String = "",
    val isReminderEnabled: Boolean = false,
    val repeatingInterval: RepeatingIntervalType = NONE,
    val isLoading: Boolean = false,
)