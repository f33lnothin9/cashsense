package ru.resodostudios.cashsense.feature.settings

import android.content.Context
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
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
import ru.resodostudios.cashsense.core.ui.R as uiR

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
    )
}

@Composable
private fun SettingsScreen(
    settingsUiState: SettingsUiState,
    onLicensesClick: () -> Unit,
    supportDynamicColor: Boolean = supportsDynamicTheming(),
    onChangeDynamicColorPreference: (useDynamicColor: Boolean) -> Unit,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
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
    onLicensesClick: () -> Unit,
) {
    item { SettingsScreenSectionTitle(stringResource(R.string.feature_settings_theme)) }
    item {
        AnimatedVisibility(supportDynamicColor) {
            CsListItem(
                headlineContent = { Text(stringResource(R.string.feature_settings_dynamic_color)) },
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

    item {
        val themeOptions = listOf(
            stringResource(R.string.feature_settings_system) to CsIcons.Android,
            stringResource(R.string.feature_settings_light) to CsIcons.LightMode,
            stringResource(R.string.feature_settings_dark) to CsIcons.DarkMode,
        )
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                .fillMaxWidth(),
        ) {
            themeOptions.forEachIndexed { index, option ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = themeOptions.size
                    ),
                    onClick = { onChangeDarkThemeConfig(DarkThemeConfig.entries[index]) },
                    selected = settings.darkThemeConfig == DarkThemeConfig.entries[index],
                    icon = {
                        SegmentedButtonDefaults.Icon(settings.darkThemeConfig == DarkThemeConfig.entries[index]) {
                            Icon(
                                imageVector = ImageVector.vectorResource(option.second),
                                contentDescription = null,
                                modifier = Modifier.size(SegmentedButtonDefaults.IconSize),
                            )
                        }
                    },
                ) {
                    Text(
                        text = option.first,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }

    item { SettingsScreenSectionTitle(stringResource(R.string.feature_settings_about)) }
    item {
        val context = LocalContext.current
        val backgroundColor = MaterialTheme.colorScheme.background.toArgb()
        CsListItem(
            headlineContent = { Text(stringResource(R.string.feature_settings_feedback)) },
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
            headlineContent = { Text(stringResource(R.string.feature_settings_privacy_policy)) },
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
            headlineContent = { Text(stringResource(R.string.feature_settings_licenses)) },
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
        val versionName = packageInfo.versionName ?: stringResource(uiR.string.none)
        CsListItem(
            headlineContent = { Text(stringResource(R.string.feature_settings_version)) },
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