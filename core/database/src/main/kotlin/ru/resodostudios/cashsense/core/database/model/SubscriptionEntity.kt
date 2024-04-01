package ru.resodostudios.cashsense.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import ru.resodostudios.cashsense.core.model.data.Subscription
import java.math.BigDecimal

@Entity(
    tableName = "subscriptions"
)
data class SubscriptionEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val amount: BigDecimal,
    val currency: String,
    @ColumnInfo(name = "payment_date")
    val paymentDate: Instant,
    @ColumnInfo(name = "notification_date")
    val notificationDate: Instant?,
    @ColumnInfo(name = "repeating_interval")
    val repeatingInterval: Long?,
)

fun SubscriptionEntity.asExternalModel() = Subscription(
    id = id,
    title = title,
    amount = amount,
    currency = currency,
    paymentDate = paymentDate,
    notificationDate = notificationDate,
    repeatingInterval = repeatingInterval,
)