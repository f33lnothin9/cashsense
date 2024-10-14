package ru.resodostudios.cashsense.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.MaterialTheme

@Composable
fun CsWearTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = wearColorScheme,
        typography = Typography,
        content = content,
    )
}
