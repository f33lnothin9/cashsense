package ru.resodostudios.cashsense.core.data.repository.offline

import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.datastore.CsPreferencesDataSource
import ru.resodostudios.cashsense.core.model.data.DarkThemeConfig
import ru.resodostudios.cashsense.core.model.data.UserData
import javax.inject.Inject

internal class OfflineUserDataRepository @Inject constructor(
    private val flickPreferencesDataSource: CsPreferencesDataSource
) : UserDataRepository {

    override val userData: Flow<UserData> =
        flickPreferencesDataSource.userData

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        flickPreferencesDataSource.setDarkThemeConfig(darkThemeConfig)
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        flickPreferencesDataSource.setDynamicColorPreference(useDynamicColor)
    }
}