package ru.resodostudios.cashsense.core.data.model

import ru.resodostudios.cashsense.core.database.model.TransactionEntity
import ru.resodostudios.cashsense.core.model.data.Transaction

fun Transaction.asEntity() = TransactionEntity(
    id = id,
    walletOwnerId = walletOwnerId,
    amount = amount,
    timestamp = timestamp,
    description = description,
    status = status,
    ignored = ignored,
    transferId = transferId,
)