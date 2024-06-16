package ru.resodostudios.cashsense.core.shortcuts.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.resodostudios.cashsense.core.shortcuts.DynamicShortcutManager
import ru.resodostudios.cashsense.core.shortcuts.ShortcutManager

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ShortcutsModule {

    @Binds
    internal abstract fun bindsShortcutManager(
        shortcutManager: DynamicShortcutManager,
    ): ShortcutManager
}
