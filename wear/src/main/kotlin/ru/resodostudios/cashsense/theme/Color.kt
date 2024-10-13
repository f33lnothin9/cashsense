package ru.resodostudios.cashsense.theme

import androidx.wear.compose.material3.ColorScheme
import ru.resodostudios.cashsense.core.designsystem.theme.errorDark
import ru.resodostudios.cashsense.core.designsystem.theme.onErrorDark
import ru.resodostudios.cashsense.core.designsystem.theme.onPrimaryDark
import ru.resodostudios.cashsense.core.designsystem.theme.onSecondaryDark
import ru.resodostudios.cashsense.core.designsystem.theme.primaryDark
import ru.resodostudios.cashsense.core.designsystem.theme.secondaryDark

internal val wearColorPalette: ColorScheme = ColorScheme(
    primary = primaryDark,
    secondary = secondaryDark,
    error = errorDark,
    onPrimary = onPrimaryDark,
    onSecondary = onSecondaryDark,
    onError = onErrorDark,
)
