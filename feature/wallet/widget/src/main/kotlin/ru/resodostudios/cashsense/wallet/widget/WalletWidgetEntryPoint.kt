package ru.resodostudios.cashsense.wallet.widget

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.resodostudios.cashsense.core.data.repository.WalletsRepository

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface WalletWidgetEntryPoint {

    fun walletsRepository(): WalletsRepository
}