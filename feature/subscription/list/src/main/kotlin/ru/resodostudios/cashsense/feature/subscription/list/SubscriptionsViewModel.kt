package ru.resodostudios.cashsense.feature.subscription.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.SubscriptionsRepository
import ru.resodostudios.cashsense.core.model.data.Subscription
import javax.inject.Inject

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val subscriptionsRepository: SubscriptionsRepository,
) : ViewModel() {

    private val shouldDisplayUndoSubscriptionState = MutableStateFlow(false)
    private val lastRemovedSubscriptionIdState = MutableStateFlow<String?>(null)
    private val selectedSubscriptionIdState = MutableStateFlow<String?>(null)

    val subscriptionsUiState: StateFlow<SubscriptionsUiState> = combine(
        subscriptionsRepository.getSubscriptions(),
        shouldDisplayUndoSubscriptionState,
        lastRemovedSubscriptionIdState,
        selectedSubscriptionIdState,
    ) { subscriptions, shouldDisplayUndoSubscription, lastRemovedSubscriptionId, selectedSubscriptionId ->
        SubscriptionsUiState.Success(
            shouldDisplayUndoSubscription,
            subscriptions.find { it.id == selectedSubscriptionId },
            subscriptions.filterNot { it.id == lastRemovedSubscriptionId },
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SubscriptionsUiState.Loading,
        )

    private fun deleteSubscription(id: String) {
        viewModelScope.launch {
            subscriptionsRepository.deleteSubscription(id)
        }
    }

    fun updateSubscriptionId(id: String) {
        selectedSubscriptionIdState.value = id
    }

    fun hideSubscription(id: String) {
        if (lastRemovedSubscriptionIdState.value != null) {
            clearUndoState()
        }
        shouldDisplayUndoSubscriptionState.value = true
        lastRemovedSubscriptionIdState.value = id
    }

    fun undoSubscriptionRemoval() {
        lastRemovedSubscriptionIdState.value = null
        shouldDisplayUndoSubscriptionState.value = false
    }

    fun clearUndoState() {
        lastRemovedSubscriptionIdState.value?.let(::deleteSubscription)
        undoSubscriptionRemoval()
    }
}

sealed interface SubscriptionsUiState {

    data object Loading : SubscriptionsUiState

    data class Success(
        val shouldDisplayUndoSubscription: Boolean,
        val selectedSubscription: Subscription?,
        val subscriptions: List<Subscription>,
    ) : SubscriptionsUiState
}