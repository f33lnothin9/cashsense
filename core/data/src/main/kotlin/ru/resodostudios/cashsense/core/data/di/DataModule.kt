package ru.resodostudios.cashsense.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.resodostudios.cashsense.core.data.repository.CategoriesRepository
import ru.resodostudios.cashsense.core.data.repository.TransactionsRepository
import ru.resodostudios.cashsense.core.data.repository.impl.CategoriesRepositoryImpl
import ru.resodostudios.cashsense.core.data.repository.impl.TransactionsRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindCategoriesRepository(
        categoriesRepositoryImpl: CategoriesRepositoryImpl
    ): CategoriesRepository

    @Binds
    fun bindTransactionsRepository(
        transactionsRepositoryImpl: TransactionsRepositoryImpl
    ): TransactionsRepository
}