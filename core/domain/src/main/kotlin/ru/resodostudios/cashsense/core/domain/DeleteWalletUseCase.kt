package ru.resodostudios.cashsense.core.domain

import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.Wallet
import javax.inject.Inject

class DeleteWalletUseCase @Inject constructor(
    private val walletsRepository: WalletsRepository,
    private val transactionsRepository: TransactionsRepository
) {
    suspend operator fun invoke(wallet: Wallet, transactions: List<Transaction>) {
        walletsRepository.deleteWalletWithTransactions(wallet, transactions)
        transactions.forEach {
            transactionsRepository.deleteTransactionCategoryCrossRef(it.id)
        }
    }
}