package ru.resodostudios.cashsense.core.network.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.resodostudios.cashsense.core.network.CsNetworkDataSource
import ru.resodostudios.cashsense.core.network.retrofit.RetrofitCsNetwork

@Module
@InstallIn(SingletonComponent::class)
internal interface NetworkDataModule {

    @Binds
    fun bindsCsNetworkDataSource(impl: RetrofitCsNetwork): CsNetworkDataSource
}