package ru.resodostudios.cashsense.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.MaterialTheme

@Composable
fun CsWearTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = wearColorPalette,
        typography = Typography,
        content = content,
    )
}
