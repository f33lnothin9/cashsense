package ru.resodostudios.cashsense.feature.settings

import android.content.Context
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import ru.resodostudios.cashsense.core.designsystem.component.CsModalBottomSheet
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.theme.supportsDynamicTheming
import ru.resodostudios.cashsense.core.model.data.DarkThemeConfig
import ru.resodostudios.cashsense.core.ui.LoadingState
import ru.resodostudios.cashsense.feature.settings.SettingsUiState.Loading
import ru.resodostudios.cashsense.feature.settings.SettingsUiState.Success

@Composable
fun SettingsBottomSheet(
    onDismiss: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val settingsUiState by viewModel.settingsUiState.collectAsStateWithLifecycle()

    SettingsBottomSheet(
        onDismiss = onDismiss,
        settingsUiState = settingsUiState,
        onChangeDynamicColorPreference = viewModel::updateDynamicColorPreference,
        onChangeDarkThemeConfig = viewModel::updateDarkThemeConfig,
    )
}

@Composable
fun SettingsBottomSheet(
    onDismiss: () -> Unit,
    settingsUiState: SettingsUiState,
    supportDynamicColor: Boolean = supportsDynamicTheming(),
    onChangeDynamicColorPreference: (useDynamicColor: Boolean) -> Unit,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
) {
    CsModalBottomSheet(
        onDismiss = { onDismiss() },
    ) {
        when (settingsUiState) {
            Loading -> LoadingState(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
            )

            is Success -> {
                Text(
                    text = stringResource(R.string.feature_settings_title),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                )
                SettingsPanel(
                    settings = settingsUiState.settings,
                    supportDynamicColor = supportDynamicColor,
                    onChangeDynamicColorPreference = onChangeDynamicColorPreference,
                    onChangeDarkThemeConfig = onChangeDarkThemeConfig,
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.SettingsPanel(
    settings: UserEditableSettings,
    supportDynamicColor: Boolean,
    onChangeDynamicColorPreference: (useDynamicColor: Boolean) -> Unit,
    onChangeDarkThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
) {
    SettingsBottomSheetSectionTitle(stringResource(R.string.feature_settings_theme))
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

    SettingsBottomSheetSectionTitle(stringResource(R.string.feature_settings_about))
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
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    CsListItem(
        headlineContent = { Text(stringResource(R.string.feature_settings_version)) },
        supportingContent = { packageInfo.versionName?.let { Text(it) } },
        leadingContent = {
            Icon(
                imageVector = ImageVector.vectorResource(CsIcons.Info),
                contentDescription = null,
            )
        },
    )
}

@Composable
private fun SettingsBottomSheetSectionTitle(text: String) {
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
private const val PRIVACY_POLICY_URL = "https://docs.google.com/document/d/e/2PACX-1vRdzezAnxwQKj7BUQwE62sBFQ_jiRE2xv4aNZgAP9ZdFH30BC9VYNhxUuxAxKEBuedMMrrR2qQp-Z9i/pub"