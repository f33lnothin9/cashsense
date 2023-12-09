package ru.resodostudios.cashsense.core.data.model

import ru.resodostudios.cashsense.core.database.model.TransactionEntity
import ru.resodostudios.cashsense.core.model.data.Transaction

fun Transaction.asEntity() = TransactionEntity(
    transactionId = transactionId,
    walletOwnerId = walletOwnerId,
    categoryId = categoryId,
    amount = amount,
    date = date,
    description = description
)