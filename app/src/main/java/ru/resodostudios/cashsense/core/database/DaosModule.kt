package ru.resodostudios.cashsense.core.database

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.resodostudios.cashsense.core.database.dao.TransactionDao

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {

    @Provides
    fun providesTransactionsDao(
        database: CsDatabase,
    ): TransactionDao = database.transactionDao()
}