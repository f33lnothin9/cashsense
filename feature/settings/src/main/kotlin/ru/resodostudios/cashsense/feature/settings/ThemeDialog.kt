package ru.resodostudios.cashsense.feature.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Palette
import ru.resodostudios.cashsense.core.model.data.DarkThemeConfig
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun ThemeDialog(
    themeConfig: DarkThemeConfig,
    themeOptions: List<String>,
    onDarkThemeConfigUpdate: (DarkThemeConfig) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = CsIcons.Outlined.Palette,
                contentDescription = null,
            )
        },
        title = {
            Text(
                text = stringResource(localesR.string.theme),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        confirmButton = {
            TextButton(onDismiss) {
                Text(stringResource(localesR.string.ok))
            }
        },
        modifier = modifier,
        text = {
            Column(Modifier.selectableGroup()) {
                themeOptions.forEachIndexed { index, label ->
                    val selected = themeConfig == DarkThemeConfig.entries[index]
                    Box(Modifier.clip(RoundedCornerShape(18.dp))) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .selectable(
                                    selected = selected,
                                    onClick = {
                                        onDarkThemeConfigUpdate(DarkThemeConfig.entries[index])
                                        onDismiss()
                                    },
                                    role = Role.RadioButton,
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = selected,
                                onClick = null,
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp),
                            )
                        }
                    }
                }
            }
        }
    )
}