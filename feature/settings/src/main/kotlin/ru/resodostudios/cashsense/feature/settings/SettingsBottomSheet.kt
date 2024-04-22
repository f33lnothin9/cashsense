package ru.resodostudios.cashsense.feature.settings

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
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
        onDismiss = { onDismiss() }
    ) {
        when (settingsUiState) {
            Loading -> LoadingState(
                Modifier
                    .height(100.dp)
                    .fillMaxWidth()
            )
            is Success -> {
                Text(
                    text = stringResource(R.string.feature_settings_title),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
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
    AnimatedVisibility(visible = supportDynamicColor) {
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(R.string.feature_settings_dynamic_color),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
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
                    thumbContent = if (settings.useDynamicColor) {
                        {
                            Icon(
                                imageVector = ImageVector.vectorResource(CsIcons.Confirm),
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    } else {
                        null
                    }
                )
            }
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
                    SegmentedButtonDefaults.Icon(active = settings.darkThemeConfig == DarkThemeConfig.entries[index]) {
                        Icon(
                            imageVector = ImageVector.vectorResource(optionIcons[index]),
                            contentDescription = null,
                            modifier = Modifier.size(SegmentedButtonDefaults.IconSize),
                        )
                    }
                }
            ) {
                Text(
                    text = label,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }

    SettingsBottomSheetSectionTitle(text = stringResource(R.string.feature_settings_about))
    val uriHandler = LocalUriHandler.current
    ListItem(
        headlineContent = { Text(text = stringResource(R.string.feature_settings_privacy_policy)) },
        leadingContent = {
            Icon(
                imageVector = ImageVector.vectorResource(CsIcons.Policy),
                contentDescription = null,
            )
        },
        modifier = Modifier.clickable { uriHandler.openUri(PRIVACY_POLICY_URL) },
    )
    val context = LocalContext.current
    ListItem(
        headlineContent = { Text(text = stringResource(R.string.feature_settings_licenses)) },
        leadingContent = {
            Icon(
                imageVector = ImageVector.vectorResource(CsIcons.HistoryEdu),
                contentDescription = null,
            )
        },
        modifier = Modifier.clickable { context.startActivity(Intent(context, OssLicensesMenuActivity::class.java)) },
    )
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    ListItem(
        headlineContent = { Text(text = stringResource(R.string.feature_settings_version)) },
        supportingContent = { Text(text = packageInfo.versionName) },
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

private const val PRIVACY_POLICY_URL = "https://docs.google.com/document/d/e/2PACX-1vRdzezAnxwQKj7BUQwE62sBFQ_jiRE2xv4aNZgAP9ZdFH30BC9VYNhxUuxAxKEBuedMMrrR2qQp-Z9i/pub"