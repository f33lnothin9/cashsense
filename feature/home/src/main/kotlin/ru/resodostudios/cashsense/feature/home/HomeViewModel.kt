package ru.resodostudios.cashsense.feature.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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

    private val baseCurrenciesState = MutableStateFlow<Set<Currency>>(emptySet())

    val financeOverviewState: StateFlow<FinanceOverviewUiState> =
        combine(
            baseCurrenciesState,
            userDataRepository.userData,
            ::Pair,
        )
            .flatMapLatest { (baseCurrencies, userData) ->
                if (baseCurrencies.isEmpty()) {
                    flowOf(FinanceOverviewUiState.NotShown)
                } else {
                    val userCurrency = Currency.getInstance(userData.currency)

                    combine(
                        currencyConversionRepository.getConvertedCurrencies(
                            baseCurrencies = baseCurrencies,
                            targetCurrency = userCurrency,
                        ),
                        getExtendedUserWallets.invoke(),
                    ) { exchangeRates, wallets ->
                        val exchangeRateMap = exchangeRates.associate { it.baseCurrency to it.exchangeRate }

                        val totalBalance = wallets.sumOf {
                            if (userCurrency == it.userWallet.currency) {
                                return@sumOf it.userWallet.currentBalance * BigDecimal.ONE
                            }
                            val exchangeRate = exchangeRateMap[it.userWallet.currency]
                                ?: return@combine FinanceOverviewUiState.NotShown

                            it.userWallet.currentBalance * exchangeRate
                        }

                        FinanceOverviewUiState.Shown(
                            totalBalance = totalBalance,
                            userCurrency = userCurrency,
                        )
                    }
                        .catch { FinanceOverviewUiState.NotShown }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = FinanceOverviewUiState.Loading,
            )


    val walletsUiState: StateFlow<WalletsUiState> = combine(
        selectedWalletId,
        getExtendedUserWallets.invoke(),
    ) { selectedWalletId, extendedUserWallets ->
        if (extendedUserWallets.isEmpty()) {
            WalletsUiState.Empty
        } else {
            baseCurrenciesState.update {
                extendedUserWallets
                    .map { it.userWallet.currency }
                    .toSet()
            }
            WalletsUiState.Success(
                selectedWalletId = selectedWalletId,
                extendedUserWallets = extendedUserWallets,
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
    ) : WalletsUiState
}

sealed interface FinanceOverviewUiState {

    data object Loading : FinanceOverviewUiState

    data object NotShown : FinanceOverviewUiState

    data class Shown(
        val totalBalance: BigDecimal,
        val userCurrency: Currency,
    ) : FinanceOverviewUiState
}

private const val SELECTED_WALLET_ID_KEY = "selectedWalletId"