package ru.resodostudios.cashsense.feature.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
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

@Composable
fun SettingsScreen(
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
                item {
                    SettingsPanel(
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
}

@Composable
private fun SettingsPanel(
    settings: UserEditableSettings,
    supportDynamicColor: Boolean,
    onChangeDynamicColorPreference: (useDynamicColor: Boolean) -> Unit,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
    onLicensesClick: () -> Unit,
) {
    SettingsScreenSectionTitle(stringResource(R.string.feature_settings_theme))
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

    val options = listOf(
        stringResource(R.string.feature_settings_system),
        stringResource(R.string.feature_settings_light),
        stringResource(R.string.feature_settings_dark),
    )
    val optionIcons = listOf(
        CsIcons.Android,
        CsIcons.LightMode,
        CsIcons.DarkMode,
    )
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            .fillMaxWidth(),
    ) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = { onChangeDarkThemeConfig(DarkThemeConfig.entries[index]) },
                selected = settings.darkThemeConfig == DarkThemeConfig.entries[index],
                icon = {
                    SegmentedButtonDefaults.Icon(settings.darkThemeConfig == DarkThemeConfig.entries[index]) {
                        Icon(
                            imageVector = ImageVector.vectorResource(optionIcons[index]),
                            contentDescription = null,
                            modifier = Modifier.size(SegmentedButtonDefaults.IconSize),
                        )
                    }
                },
                colors = SegmentedButtonDefaults.colors(
                    inactiveContainerColor = Color.Transparent,
                ),
            ) {
                Text(
                    text = label,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }

    SettingsScreenSectionTitle(stringResource(R.string.feature_settings_about))
    val uriHandler = LocalUriHandler.current
    CsListItem(
        headlineContent = { Text(stringResource(R.string.feature_settings_privacy_policy)) },
        leadingContent = {
            Icon(
                imageVector = ImageVector.vectorResource(CsIcons.Policy),
                contentDescription = null,
            )
        },
        onClick = { uriHandler.openUri(PRIVACY_POLICY_URL) },
    )
    val context = LocalContext.current
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
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    CsListItem(
        headlineContent = { Text(stringResource(R.string.feature_settings_version)) },
        supportingContent = { Text(packageInfo.versionName) },
        leadingContent = {
            Icon(
                imageVector = ImageVector.vectorResource(CsIcons.Info),
                contentDescription = null,
            )
        },
    )
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

private const val PRIVACY_POLICY_URL =
    "https://docs.google.com/document/d/e/2PACX-1vRdzezAnxwQKj7BUQwE62sBFQ_jiRE2xv4aNZgAP9ZdFH30BC9VYNhxUuxAxKEBuedMMrrR2qQp-Z9i/pub"