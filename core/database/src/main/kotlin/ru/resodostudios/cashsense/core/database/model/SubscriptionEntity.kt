package ru.resodostudios.cashsense.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import ru.resodostudios.cashsense.core.model.data.Currency
import ru.resodostudios.cashsense.core.model.data.Subscription
import java.math.BigDecimal
import java.util.UUID

@Entity(
    tableName = "subscriptions"
)
data class SubscriptionEntity(
    @PrimaryKey
    val subscriptionId: UUID,
    val title: String,
    val amount: BigDecimal,
    val currency: Currency,
    val paymentDate: Instant,
    val notificationDate: Instant?,
    val repeatingInterval: Long?
)

fun SubscriptionEntity.asExternalModel() = Subscription(
    subscriptionId = subscriptionId,
    title = title,
    amount = amount,
    currency = currency,
    paymentDate = paymentDate,
    notificationDate = notificationDate,
    repeatingInterval = repeatingInterval
)