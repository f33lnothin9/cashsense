package ru.resodostudios.cashsense.feature.subscriptions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

    val subscriptionUiState: StateFlow<SubscriptionUiState> =
        subscriptionsRepository.getSubscription(UUID.fromString(subscriptionId))
            .map<Subscription, SubscriptionUiState>(SubscriptionUiState::Success)
            .onStart { emit(SubscriptionUiState.Loading) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SubscriptionUiState.Loading
            )

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