package ru.resodostudios.cashsense.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.resodostudios.cashsense.core.data.repository.CategoriesRepository
import ru.resodostudios.cashsense.core.data.repository.SubscriptionsRepository
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository
import ru.resodostudios.cashsense.core.data.repository.offline.OfflineCategoriesRepository
import ru.resodostudios.cashsense.core.data.repository.offline.OfflineSubscriptionsRepository
import ru.resodostudios.cashsense.core.data.repository.offline.OfflineTransactionRepository
import ru.resodostudios.cashsense.core.data.repository.offline.OfflineUserDataRepository
import ru.resodostudios.cashsense.core.data.repository.offline.OfflineWalletsRepository
import ru.resodostudios.cashsense.core.data.util.AlarmScheduler
import ru.resodostudios.cashsense.core.data.util.NotificationAlarmScheduler
import ru.resodostudios.cashsense.core.data.util.TimeZoneBroadcastMonitor
import ru.resodostudios.cashsense.core.data.util.TimeZoneMonitor

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    internal abstract fun bindsCategoriesRepository(
        categoriesRepository: OfflineCategoriesRepository,
    ): CategoriesRepository

    @Binds
    internal abstract fun bindsTransactionsRepository(
        transactionsRepository: OfflineTransactionRepository,
    ): TransactionsRepository

    @Binds
    internal abstract fun bindsWalletsRepository(
        walletsRepository: OfflineWalletsRepository,
    ): WalletsRepository

    @Binds
    internal abstract fun bindsSubscriptionsRepository(
        subscriptionsRepository: OfflineSubscriptionsRepository,
    ): SubscriptionsRepository

    @Binds
    internal abstract fun bindsUserDataRepository(
        userDataRepository: OfflineUserDataRepository,
    ): UserDataRepository

    @Binds
    internal abstract fun bindsTimeZoneMonitor(
        timeZoneMonitor: TimeZoneBroadcastMonitor,
    ): TimeZoneMonitor

    @Binds
    internal abstract fun bindsNotificationAlarmScheduler(
        notificationAlarmScheduler: NotificationAlarmScheduler,
    ): AlarmScheduler
}