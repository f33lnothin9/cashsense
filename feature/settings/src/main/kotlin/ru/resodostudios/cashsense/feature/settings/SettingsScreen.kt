package ru.resodostudios.cashsense.feature.settings

import android.content.Context
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.theme.supportsDynamicTheming
import ru.resodostudios.cashsense.core.model.data.DarkThemeConfig
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.feature.settings.SettingsUiState.Loading
import ru.resodostudios.cashsense.feature.settings.SettingsUiState.Success
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun SettingsScreen(
    onLicensesClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val settingsUiState by viewModel.settingsUiState.collectAsStateWithLifecycle()

    SettingsScreen(
        settingsUiState = settingsUiState,
        onLicensesClick = onLicensesClick,
        onChangeDynamicColorPreference = viewModel::updateDynamicColorPreference,
        onChangeDarkThemeConfig = viewModel::updateDarkThemeConfig,
        onChangeCurrency = viewModel::updateCurrency,
    )
}

@Composable
private fun SettingsScreen(
    settingsUiState: SettingsUiState,
    onLicensesClick: () -> Unit,
    supportDynamicColor: Boolean = supportsDynamicTheming(),
    onChangeDynamicColorPreference: (useDynamicColor: Boolean) -> Unit,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
    onChangeCurrency: (currency: String) -> Unit,
) {
    when (settingsUiState) {
        Loading -> LoadingState(Modifier.fillMaxSize())
        is Success -> {
            LazyColumn {
                settings(
                    settings = settingsUiState.settings,
                    supportDynamicColor = supportDynamicColor,
                    onChangeDynamicColorPreference = onChangeDynamicColorPreference,
                    onChangeDarkThemeConfig = onChangeDarkThemeConfig,
                    onChangeCurrency = onChangeCurrency,
                    onLicensesClick = onLicensesClick,
                )
            }
        }
    }
}

private fun LazyListScope.settings(
    settings: UserEditableSettings,
    supportDynamicColor: Boolean,
    onChangeDynamicColorPreference: (useDynamicColor: Boolean) -> Unit,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
    onChangeCurrency: (currency: String) -> Unit,
    onLicensesClick: () -> Unit,
) {
    item { SettingsScreenSectionTitle(stringResource(localesR.string.settings_general)) }
    item {
        var showCurrencyDialog by rememberSaveable { mutableStateOf(false) }
        val supportingText = settings.currency.ifEmpty {
            stringResource(localesR.string.choose_currency)
        }
        CsListItem(
            headlineContent = { Text(stringResource(localesR.string.currency)) },
            leadingContent = {
                Icon(
                    imageVector = ImageVector.vectorResource(CsIcons.UniversalCurrencyAlt),
                    contentDescription = null,
                )
            },
            supportingContent = { Text(supportingText) },
            onClick = { showCurrencyDialog = true },
        )
        if (showCurrencyDialog) {
            CurrencyDialog(
                currencyCode = settings.currency,
                onDismiss = { showCurrencyDialog = false },
                onCurrencyClick = { onChangeCurrency(it) },
            )
        }
    }
    item { SettingsScreenSectionTitle(stringResource(localesR.string.settings_appearance)) }
    item {
        val themeOptions = listOf(
            stringResource(localesR.string.theme_system_default),
            stringResource(localesR.string.theme_light),
            stringResource(localesR.string.theme_dark),
        )
        var showThemeDialog by rememberSaveable { mutableStateOf(false) }

        CsListItem(
            headlineContent = { Text(stringResource(localesR.string.theme)) },
            leadingContent = {
                Icon(
                    imageVector = ImageVector.vectorResource(CsIcons.Palette),
                    contentDescription = null,
                )
            },
            supportingContent = { Text(themeOptions.elementAt(settings.darkThemeConfig.ordinal)) },
            onClick = { showThemeDialog = true },
        )

        if (showThemeDialog) {
            ThemeDialog(
                themeConfig = settings.darkThemeConfig,
                themeOptions = themeOptions,
                onThemeClick = { onChangeDarkThemeConfig(DarkThemeConfig.entries[it]) },
                onDismiss = { showThemeDialog = false },
            )
        }
    }
    item {
        AnimatedVisibility(supportDynamicColor) {
            CsListItem(
                headlineContent = { Text(stringResource(localesR.string.dynamic_color)) },
                leadingContent = {
                    Icon(
                        imageVector = ImageVector.vectorResource(CsIcons.FormatPaint),
                        contentDescription = null,
                    )
                },
                trailingContent = {
                    Switch(
                        checked = settings.useDynamicColor,
                        onCheckedChange = { onChangeDynamicColorPreference(it) },
                    )
                },
            )
        }
    }

    item { SettingsScreenSectionTitle(stringResource(localesR.string.about)) }
    item {
        val context = LocalContext.current
        val backgroundColor = MaterialTheme.colorScheme.background.toArgb()
        CsListItem(
            headlineContent = { Text(stringResource(localesR.string.feedback)) },
            leadingContent = {
                Icon(
                    imageVector = ImageVector.vectorResource(CsIcons.Feedback),
                    contentDescription = null,
                )
            },
            onClick = {
                launchCustomChromeTab(
                    context = context,
                    uri = Uri.parse(FEEDBACK_URL),
                    toolbarColor = backgroundColor,
                )
            },
        )
    }
    item {
        val context = LocalContext.current
        val backgroundColor = MaterialTheme.colorScheme.background.toArgb()
        CsListItem(
            headlineContent = { Text(stringResource(localesR.string.privacy_policy)) },
            leadingContent = {
                Icon(
                    imageVector = ImageVector.vectorResource(CsIcons.Policy),
                    contentDescription = null,
                )
            },
            onClick = {
                launchCustomChromeTab(
                    context = context,
                    uri = Uri.parse(PRIVACY_POLICY_URL),
                    toolbarColor = backgroundColor,
                )
            },
        )
    }
    item {
        CsListItem(
            headlineContent = { Text(stringResource(localesR.string.licenses)) },
            leadingContent = {
                Icon(
                    imageVector = ImageVector.vectorResource(CsIcons.HistoryEdu),
                    contentDescription = null,
                )
            },
            onClick = onLicensesClick,
        )
    }
    item {
        val context = LocalContext.current
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val versionName = packageInfo.versionName ?: stringResource(localesR.string.none)
        CsListItem(
            headlineContent = { Text(stringResource(localesR.string.version)) },
            supportingContent = { Text(versionName) },
            leadingContent = {
                Icon(
                    imageVector = ImageVector.vectorResource(CsIcons.Info),
                    contentDescription = null,
                )
            },
        )
    }
}

@Composable
private fun SettingsScreenSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(top = 32.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

private fun launchCustomChromeTab(context: Context, uri: Uri, @ColorInt toolbarColor: Int) {
    val customTabBarColor = CustomTabColorSchemeParams.Builder()
        .setToolbarColor(toolbarColor).build()
    val customTabsIntent = CustomTabsIntent.Builder()
        .setDefaultColorSchemeParams(customTabBarColor)
        .build()

    customTabsIntent.launchUrl(context, uri)
}

private const val FEEDBACK_URL = "https://forms.gle/kQcVkZHtgD6ZMTeX7"
private const val PRIVACY_POLICY_URL = "https://trusted-cowl-779.notion.site/Privacy-Policy-65accc6cf3714f289392ae1ffee96bae?pvs=4"