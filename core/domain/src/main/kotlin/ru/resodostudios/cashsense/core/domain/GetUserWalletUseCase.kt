package ru.resodostudios.cashsense.core.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.model.data.UserWallet
import javax.inject.Inject

class GetUserWalletUseCase @Inject constructor(
    private val walletsRepository: WalletsRepository,
    private val userDataRepository: UserDataRepository,
) {

    operator fun invoke(walletId: String): Flow<UserWallet> = combine(
        walletsRepository.getWalletWithTransactionsAndCategories(walletId),
        userDataRepository.userData,
    ) { walletWithTransactionsAndCategories, userData ->
        val currentBalance = walletWithTransactionsAndCategories.transactionsWithCategories
            .sumOf { it.transaction.amount }
            .plus(walletWithTransactionsAndCategories.wallet.initialBalance)
        UserWallet(
            id = walletWithTransactionsAndCategories.wallet.id,
            title = walletWithTransactionsAndCategories.wallet.title,
            initialBalance = walletWithTransactionsAndCategories.wallet.initialBalance,
            currentBalance = currentBalance,
            currency = walletWithTransactionsAndCategories.wallet.currency,
            isPrimary = walletWithTransactionsAndCategories.wallet.id == userData.primaryWalletId,
        )
    }
}