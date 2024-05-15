package ru.resodostudios.cashsense.core.notifications.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.resodostudios.cashsense.core.notifications.Notifier
import ru.resodostudios.cashsense.core.notifications.SystemTrayNotifier

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NotificationsModule {

    @Binds
    abstract fun bindNotifier(
        notifier: SystemTrayNotifier,
    ): Notifier
}
