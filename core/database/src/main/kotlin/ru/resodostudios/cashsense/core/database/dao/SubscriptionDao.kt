package ru.resodostudios.cashsense.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.database.model.SubscriptionEntity
import java.util.UUID

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions WHERE subscriptionId = :subscriptionId")
    fun getSubscriptionEntity(subscriptionId: UUID): Flow<SubscriptionEntity>

    @Query("SELECT * FROM subscriptions ORDER BY paymentDate DESC")
    fun getSubscriptionEntities(): Flow<List<SubscriptionEntity>>

    @Upsert
    suspend fun upsertSubscription(subscription: SubscriptionEntity)

    @Delete
    suspend fun deleteSubscription(subscription: SubscriptionEntity)
}