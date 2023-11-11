package ru.resodostudios.cashsense.core.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesCsDatabase(
        @ApplicationContext context: Context,
    ): CsDatabase = Room.databaseBuilder(
        context,
        CsDatabase::class.java,
        "cs-database",
    ).build()
}