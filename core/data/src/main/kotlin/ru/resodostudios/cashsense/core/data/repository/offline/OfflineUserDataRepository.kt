package ru.resodostudios.cashsense.core.data.repository.offline

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.resodostudios.cashsense.core.data.repository.UserDataRepository
import ru.resodostudios.cashsense.core.datastore.CsPreferencesDataSource
import ru.resodostudios.cashsense.core.model.data.DarkThemeConfig
import ru.resodostudios.cashsense.core.model.data.UserData
import ru.resodostudios.cashsense.core.network.CsDispatchers.IO
import ru.resodostudios.cashsense.core.network.Dispatcher
import ru.resodostudios.cashsense.core.network.di.ApplicationScope
import ru.resodostudios.cashsense.core.shortcuts.ShortcutManager
import javax.inject.Inject

internal class OfflineUserDataRepository @Inject constructor(
    private val csPreferencesDataSource: CsPreferencesDataSource,
    private val shortcutManager: ShortcutManager,
    @ApplicationScope private val appScope: CoroutineScope,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : UserDataRepository {

    override val userData: Flow<UserData> = csPreferencesDataSource.userData

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) =
        csPreferencesDataSource.setDarkThemeConfig(darkThemeConfig)

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) =
        csPreferencesDataSource.setDynamicColorPreference(useDynamicColor)

    override suspend fun setPrimaryWalletId(id: String, isPrimary: Boolean) {
        appScope.launch(ioDispatcher) {
            if (isPrimary) {
                csPreferencesDataSource.setPrimaryWalletId(id)
                shortcutManager.addTransactionShortcut(id)
            } else if (csPreferencesDataSource.userData.first().primaryWalletId == id) {
                csPreferencesDataSource.setPrimaryWalletId("")
                shortcutManager.removeShortcuts()
            }
        }.join()
    }

    override suspend fun setCurrency(currency: String) =
        csPreferencesDataSource.setCurrency(currency)
}