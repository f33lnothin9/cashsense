package ru.resodostudios.cashsense.core.database.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.resodostudios.cashsense.core.database.CsDatabase
import ru.resodostudios.cashsense.core.database.dao.CategoryDao
import ru.resodostudios.cashsense.core.database.dao.SubscriptionDao
import ru.resodostudios.cashsense.core.database.dao.TransactionDao
import ru.resodostudios.cashsense.core.database.dao.WalletDao

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {

    @Provides
    fun providesTransactionDao(
        database: CsDatabase,
    ): TransactionDao = database.transactionDao()

    @Provides
    fun providesCategoryDao(
        database: CsDatabase,
    ): CategoryDao = database.categoryDao()

    @Provides
    fun providesWalletDao(
        database: CsDatabase,
    ): WalletDao = database.walletDao()

    @Provides
    fun providesSubscriptionDao(
        database: CsDatabase,
    ): SubscriptionDao = database.subscriptionDao()
}