package ru.resodostudios.cashsense.core.data.model

import ru.resodostudios.cashsense.core.database.model.WalletEntity
import ru.resodostudios.cashsense.core.model.data.Wallet

fun Wallet.asEntity() = WalletEntity(
    walletId = walletId,
    title = title,
    startBalance = startBalance,
    currency = currency
)