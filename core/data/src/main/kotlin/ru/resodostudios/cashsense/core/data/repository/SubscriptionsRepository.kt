package ru.resodostudios.cashsense.core.data.repository

import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.model.data.Subscription
import java.util.UUID

interface SubscriptionsRepository {
    fun getSubscription(subscriptionId: UUID): Flow<Subscription>

    fun getSubscriptions(): Flow<List<Subscription>>

    suspend fun upsertSubscription(subscription: Subscription)

    suspend fun deleteSubscription(subscription: Subscription)
}