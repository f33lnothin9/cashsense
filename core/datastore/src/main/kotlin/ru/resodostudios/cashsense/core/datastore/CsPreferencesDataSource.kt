package ru.resodostudios.cashsense.core.datastore

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.map
import ru.resodostudios.cashsense.core.model.data.DarkThemeConfig
import ru.resodostudios.cashsense.core.model.data.DarkThemeConfig.DARK
import ru.resodostudios.cashsense.core.model.data.DarkThemeConfig.FOLLOW_SYSTEM
import ru.resodostudios.cashsense.core.model.data.DarkThemeConfig.LIGHT
import ru.resodostudios.cashsense.core.model.data.UserData
import javax.inject.Inject

class CsPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>,
) {
    val userData = userPreferences.data
        .map {
            UserData(
                darkThemeConfig = when (it.darkThemeConfig) {
                    null,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_UNSPECIFIED,
                    DarkThemeConfigProto.UNRECOGNIZED,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM,
                    -> FOLLOW_SYSTEM

                    DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT -> LIGHT
                    DarkThemeConfigProto.DARK_THEME_CONFIG_DARK -> DARK
                },
                useDynamicColor = it.useDynamicColor,
                primaryWalletId = it.primaryWalletId,
            )
        }

    suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        userPreferences.updateData {
            it.copy { this.useDynamicColor = useDynamicColor }
        }
    }

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        userPreferences.updateData {
            it.copy {
                this.darkThemeConfig = when (darkThemeConfig) {
                    FOLLOW_SYSTEM -> DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
                    LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
                    DARK -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
                }
            }
        }
    }

    suspend fun setPrimaryWalletId(id: String) {
        userPreferences.updateData {
            it.copy { this.primaryWalletId = id }
        }
    }
}