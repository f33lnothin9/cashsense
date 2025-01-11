package ru.resodostudios.cashsense.core.database.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.resodostudios.cashsense.core.database.CsDatabase
import ru.resodostudios.cashsense.core.database.dao.CategoryDao
import ru.resodostudios.cashsense.core.database.dao.CurrencyConversionDao
import ru.resodostudios.cashsense.core.database.dao.SubscriptionDao
import ru.resodostudios.cashsense.core.database.dao.TransactionDao
import ru.resodostudios.cashsense.core.database.dao.WalletDao

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {

    @Provides
    fun providesCategoryDao(
        database: CsDatabase,
    ): CategoryDao = database.categoryDao()

    @Provides
    fun providesCurrencyConversionDao(
        database: CsDatabase,
    ): CurrencyConversionDao = database.currencyConversionDao()

    @Provides
    fun providesSubscriptionDao(
        database: CsDatabase,
    ): SubscriptionDao = database.subscriptionDao()

    @Provides
    fun providesTransactionDao(
        database: CsDatabase,
    ): TransactionDao = database.transactionDao()

    @Provides
    fun providesWalletDao(
        database: CsDatabase,
    ): WalletDao = database.walletDao()
}