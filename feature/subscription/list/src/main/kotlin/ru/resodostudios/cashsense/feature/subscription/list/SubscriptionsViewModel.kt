package ru.resodostudios.cashsense.feature.subscription.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.SubscriptionsRepository
import ru.resodostudios.cashsense.core.model.data.Subscription
import javax.inject.Inject

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val subscriptionsRepository: SubscriptionsRepository,
) : ViewModel() {

    val subscriptionsUiState: StateFlow<SubscriptionsUiState> =
        subscriptionsRepository.getSubscriptions()
            .map<List<Subscription>, SubscriptionsUiState>(SubscriptionsUiState::Success)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SubscriptionsUiState.Loading
            )

    fun deleteSubscription(id: String) {
        viewModelScope.launch {
            subscriptionsRepository.deleteSubscription(id)
        }
    }
}

sealed interface SubscriptionsUiState {

    data object Loading : SubscriptionsUiState

    data class Success(
        val subscriptions: List<Subscription>,
    ) : SubscriptionsUiState
}