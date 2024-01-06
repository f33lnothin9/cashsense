package ru.resodostudios.cashsense.core.data.model

import ru.resodostudios.cashsense.core.database.model.SubscriptionEntity
import ru.resodostudios.cashsense.core.model.data.Subscription

fun Subscription.asEntity() = SubscriptionEntity(
    subscriptionId = subscriptionId,
    title = title,
    amount = amount,
    paymentDate = paymentDate,
    notificationDate = notificationDate
)