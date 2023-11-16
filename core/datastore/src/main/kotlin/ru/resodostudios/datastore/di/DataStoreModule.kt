package ru.resodostudios.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import ru.resodostudios.datastore.UserPreferencesSerializer
import ru.resodostudios.flick.core.datastore.UserPreferences
import ru.resodostudios.flick.core.network.Dispatcher
import ru.resodostudios.flick.core.network.FlickDispatchers.IO
import ru.resodostudios.flick.core.network.di.ApplicationScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun providesUserPreferencesDataStore(
        @ApplicationContext context: Context,
        @Dispatcher(IO) ioDispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope,
        userPreferencesSerializer: UserPreferencesSerializer,
    ): DataStore<UserPreferences> =
        DataStoreFactory.create(
            serializer = userPreferencesSerializer,
            scope = CoroutineScope(scope.coroutineContext + ioDispatcher)
        ) {
            context.dataStoreFile("user_preferences.pb")
        }
}
