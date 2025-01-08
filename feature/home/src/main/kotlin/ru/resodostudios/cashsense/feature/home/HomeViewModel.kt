package ru.resodostudios.cashsense.feature.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import ru.resodostudios.cashsense.core.data.repository.CurrencyConversionRepository
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.domain.GetExtendedUserWalletsUseCase
import ru.resodostudios.cashsense.core.model.data.ExtendedUserWallet
import ru.resodostudios.cashsense.feature.home.navigation.HomeRoute
import java.math.BigDecimal
import java.util.Currency
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    getExtendedUserWallets: GetExtendedUserWalletsUseCase,
    private val currencyConversionRepository: CurrencyConversionRepository,
    userDataRepository: UserDataRepository,
) : ViewModel() {

    private val homeDestination: HomeRoute = savedStateHandle.toRoute()
    private val selectedWalletId = savedStateHandle.getStateFlow(
        key = SELECTED_WALLET_ID_KEY,
        initialValue = homeDestination.walletId,
    )

    val walletsUiState: StateFlow<WalletsUiState> = combine(
        selectedWalletId,
        getExtendedUserWallets.invoke(),
        userDataRepository.userData,
    ) { selectedWalletId, extendedUserWallets, userData ->
        if (extendedUserWallets.isEmpty()) {
            WalletsUiState.Empty
        } else {
            val userCurrency = Currency.getInstance(userData.currency)
            val baseCurrencies = extendedUserWallets
                .map { it.userWallet.currency }
                .toSet()
                .minus(userCurrency)
            val totalBalance = if (!baseCurrencies.all { it == userCurrency }) {
                val currencyExchangeRates = currencyConversionRepository.getConvertedCurrencies(
                    baseCurrencies = baseCurrencies,
                    targetCurrency = userCurrency,
                ).first()
                if (currencyExchangeRates.isNotEmpty() && currencyExchangeRates.size == baseCurrencies.size) {
                    extendedUserWallets.sumOf {
                        it.userWallet.currentBalance * (currencyExchangeRates.find { rate ->
                            rate.baseCurrency == it.userWallet.currency
                        }?.exchangeRate ?: BigDecimal.ONE)
                    }
                } else null
            } else {
                extendedUserWallets.sumOf { it.userWallet.currentBalance }
            }
            WalletsUiState.Success(
                selectedWalletId = selectedWalletId,
                extendedUserWallets = extendedUserWallets,
                totalBalance = totalBalance,
                userCurrency = userCurrency,
            )
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WalletsUiState.Loading,
        )

    fun onWalletClick(walletId: String?) {
        savedStateHandle[SELECTED_WALLET_ID_KEY] = walletId
    }
}

sealed interface WalletsUiState {

    data object Loading : WalletsUiState

    data object Empty : WalletsUiState

    data class Success(
        val selectedWalletId: String?,
        val extendedUserWallets: List<ExtendedUserWallet>,
        val totalBalance: BigDecimal?,
        val userCurrency: Currency,
    ) : WalletsUiState
}

private const val SELECTED_WALLET_ID_KEY = "selectedWalletId"