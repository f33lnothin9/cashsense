package ru.resodostudios.cashsense.core.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.model.data.ExtendedUserWallet
import ru.resodostudios.cashsense.core.model.data.UserWallet
import javax.inject.Inject

class GetExtendedUserWalletUseCase @Inject constructor(
    private val walletsRepository: WalletsRepository,
    private val userDataRepository: UserDataRepository,
) {

    operator fun invoke(walletId: String): Flow<ExtendedUserWallet> = combine(
        walletsRepository.getWalletWithTransactionsAndCategories(walletId),
        userDataRepository.userData,
    ) { extendedWallet, userData ->
        val currentBalance = extendedWallet.transactionsWithCategories
            .sumOf { it.transaction.amount }
            .plus(extendedWallet.wallet.initialBalance)
        ExtendedUserWallet(
            userWallet = UserWallet(
                id = extendedWallet.wallet.id,
                title = extendedWallet.wallet.title,
                initialBalance = extendedWallet.wallet.initialBalance,
                currentBalance = currentBalance,
                currency = extendedWallet.wallet.currency,
                isPrimary = extendedWallet.wallet.id == userData.primaryWalletId,
            ),
            transactionsWithCategories = extendedWallet.transactionsWithCategories
                .sortedByDescending { it.transaction.timestamp },
        )
    }
}