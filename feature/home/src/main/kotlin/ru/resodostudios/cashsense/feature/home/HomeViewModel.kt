package ru.resodostudios.cashsense.feature.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.resodostudios.cashsense.core.data.repository.CurrencyConversionRepository
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.domain.GetExtendedUserWalletsUseCase
import ru.resodostudios.cashsense.core.model.data.ExtendedUserWallet
import ru.resodostudios.cashsense.core.ui.util.getZonedDateTime
import ru.resodostudios.cashsense.core.ui.util.isInCurrentMonthAndYear
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

    val totalBalanceUiState: StateFlow<TotalBalanceUiState> = combine(
        getExtendedUserWallets.invoke(),
        userDataRepository.userData,
    ) { wallets, userData ->
        val baseCurrencies = wallets.mapTo(HashSet()) { it.userWallet.currency }
        val userCurrency = Currency.getInstance(userData.currency)
        Triple(baseCurrencies, userCurrency, wallets)
    }
        .flatMapLatest { (baseCurrencies, userCurrency, wallets) ->
            flow {
                emit(TotalBalanceUiState.Loading)

                if (baseCurrencies.isEmpty()) {
                    emit(TotalBalanceUiState.NotShown)
                } else {
                    currencyConversionRepository.getConvertedCurrencies(
                        baseCurrencies = baseCurrencies,
                        targetCurrency = userCurrency,
                    )
                        .map { exchangeRates ->
                            val exchangeRateMap = exchangeRates
                                .associate { it.baseCurrency to it.exchangeRate }

                            val totalBalance = wallets.sumOf {
                                if (userCurrency == it.userWallet.currency) {
                                    return@sumOf it.userWallet.currentBalance
                                }
                                val exchangeRate = exchangeRateMap[it.userWallet.currency]
                                    ?: return@map TotalBalanceUiState.NotShown

                                it.userWallet.currentBalance * exchangeRate
                            }

                            val allTransactions = wallets.flatMap { wallet ->
                                wallet.transactionsWithCategories.map { it.transaction }
                            }

                            val monthlyTransactions = allTransactions.filter {
                                it.timestamp.getZonedDateTime()
                                    .isInCurrentMonthAndYear() && !it.ignored
                            }

                            val (expenses, income) = monthlyTransactions.partition { it.amount.signum() == -1 }

                            val totalExpenses = expenses.sumOf { it.amount }.abs()
                            val totalIncome = income.sumOf { it.amount }

                            TotalBalanceUiState.Shown(
                                amount = totalBalance,
                                userCurrency = userCurrency,
                                shouldShowBadIndicator = totalIncome < totalExpenses,
                            )
                        }
                        .catch { emit(TotalBalanceUiState.NotShown) }
                        .collect { emit(it) }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TotalBalanceUiState.Loading,
        )


    val walletsUiState: StateFlow<WalletsUiState> = combine(
        selectedWalletId,
        getExtendedUserWallets.invoke(),
    ) { selectedWalletId, extendedUserWallets ->
        if (extendedUserWallets.isEmpty()) {
            WalletsUiState.Empty
        } else {
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

sealed interface TotalBalanceUiState {

    data object Loading : TotalBalanceUiState

    data object NotShown : TotalBalanceUiState

    data class Shown(
        val amount: BigDecimal,
        val userCurrency: Currency,
        val shouldShowBadIndicator: Boolean,
    ) : TotalBalanceUiState
}

private const val SELECTED_WALLET_ID_KEY = "selectedWalletId"