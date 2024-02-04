package ru.resodostudios.cashsense.core.data.repository.offline

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.resodostudios.cashsense.core.data.model.asEntity
import ru.resodostudios.cashsense.core.data.repository.SubscriptionsRepository
import ru.resodostudios.cashsense.core.database.dao.SubscriptionDao
import ru.resodostudios.cashsense.core.database.model.SubscriptionEntity
import ru.resodostudios.cashsense.core.database.model.asExternalModel
import ru.resodostudios.cashsense.core.model.data.Subscription
import javax.inject.Inject

class OfflineSubscriptionsRepository @Inject constructor(
    private val subscriptionDao: SubscriptionDao
) : SubscriptionsRepository {

    override fun getSubscription(id: String): Flow<Subscription> =
        subscriptionDao.getSubscriptionEntity(id).map { it.asExternalModel() }

    override fun getSubscriptions(): Flow<List<Subscription>> =
        subscriptionDao.getSubscriptionEntities().map { it.map(SubscriptionEntity::asExternalModel) }

    override suspend fun upsertSubscription(subscription: Subscription) =
        subscriptionDao.upsertSubscription(subscription.asEntity())

    override suspend fun deleteSubscription(subscription: Subscription) =
        subscriptionDao.deleteSubscription(subscription.asEntity())
}