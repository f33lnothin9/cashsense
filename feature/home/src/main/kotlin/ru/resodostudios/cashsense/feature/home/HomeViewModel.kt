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
import kotlinx.coroutines.flow.stateIn
import ru.resodostudios.cashsense.core.data.repository.CurrencyConversionRepository
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.domain.GetExtendedUserWalletsUseCase
import ru.resodostudios.cashsense.core.model.data.ExtendedUserWallet
import ru.resodostudios.cashsense.core.model.data.Transaction
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
    walletRepository: WalletsRepository,
) : ViewModel() {

    private val homeDestination: HomeRoute = savedStateHandle.toRoute()
    private val selectedWalletId = savedStateHandle.getStateFlow(
        key = SELECTED_WALLET_ID_KEY,
        initialValue = homeDestination.walletId,
    )

    val totalBalanceUiState: StateFlow<TotalBalanceUiState> = combine(
        walletRepository.getDistinctCurrencies(),
        userDataRepository.userData,
    ) { currencies, userData ->
        val userCurrency = Currency.getInstance(userData.currency)
        currencies to userCurrency
    }
        .flatMapLatest { (baseCurrencies, userCurrency) ->
            flow {
                emit(TotalBalanceUiState.Loading)

                if (baseCurrencies.isEmpty()) {
                    emit(TotalBalanceUiState.NotShown)
                } else {
                    val shouldShowApproximately = !baseCurrencies.all { it == userCurrency }

                    combine(
                        getExtendedUserWallets.invoke(),
                        currencyConversionRepository.getConvertedCurrencies(
                            baseCurrencies = baseCurrencies.toSet(),
                            targetCurrency = userCurrency,
                        ),
                    ) { wallets, exchangeRates ->
                        val exchangeRateMap = exchangeRates
                            .associate { it.baseCurrency to it.exchangeRate }

                        var totalBalance = BigDecimal.ZERO
                        val allTransactions = mutableListOf<Transaction>()

                        wallets.forEach { wallet ->
                            val balance = wallet.userWallet.currentBalance
                            val currency = wallet.userWallet.currency

                            val convertedBalance = if (userCurrency == currency) {
                                balance
                            } else {
                                exchangeRateMap[currency]?.let { rate -> balance * rate }
                                    ?: return@combine TotalBalanceUiState.NotShown
                            }
                            totalBalance += convertedBalance
                            allTransactions.addAll(wallet.transactionsWithCategories.map { it.transaction })
                        }

                        val (totalExpenses, totalIncome) = allTransactions
                            .asSequence()
                            .filter { !it.ignored && it.timestamp.isInCurrentMonthAndYear() }
                            .fold(BigDecimal.ZERO to BigDecimal.ZERO) { (expenses, income), transaction ->
                                if (transaction.amount.signum() < 0) {
                                    expenses + transaction.amount to income
                                } else {
                                    expenses to income + transaction.amount
                                }
                            }

                        TotalBalanceUiState.Shown(
                            amount = totalBalance,
                            userCurrency = userCurrency,
                            shouldShowBadIndicator = totalIncome < totalExpenses.abs(),
                            shouldShowApproximately = shouldShowApproximately,
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

    val homeUiState: StateFlow<HomeUiState> = combine(
        selectedWalletId,
        getExtendedUserWallets.invoke(),
    ) { selectedWalletId, extendedUserWallets ->
        if (extendedUserWallets.isEmpty()) {
            HomeUiState.Empty
        } else {
            HomeUiState.Success(
                selectedWalletId = selectedWalletId,
                extendedUserWallets = extendedUserWallets,
            )
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Loading,
        )

    fun onWalletClick(walletId: String?) {
        savedStateHandle[SELECTED_WALLET_ID_KEY] = walletId
    }
}

sealed interface HomeUiState {

    data object Loading : HomeUiState

    data object Empty : HomeUiState

    data class Success(
        val selectedWalletId: String?,
        val extendedUserWallets: List<ExtendedUserWallet>,
    ) : HomeUiState
}

sealed interface TotalBalanceUiState {

    data object Loading : TotalBalanceUiState

    data object NotShown : TotalBalanceUiState

    data class Shown(
        val amount: BigDecimal,
        val userCurrency: Currency,
        val shouldShowBadIndicator: Boolean,
        val shouldShowApproximately: Boolean,
    ) : TotalBalanceUiState
}

private const val SELECTED_WALLET_ID_KEY = "selectedWalletId"